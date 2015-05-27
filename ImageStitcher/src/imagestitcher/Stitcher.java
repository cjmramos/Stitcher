/*
 * Parts of code created by 2011-2014, Peter Abeles
 */
package imagestitcher;

/**
 *
 * @author cjhay
 */
import boofcv.abst.feature.associate.AssociateDescription;
import boofcv.abst.feature.associate.ScoreAssociation;
import boofcv.abst.feature.detdesc.DetectDescribePoint;
import boofcv.abst.feature.detect.interest.ConfigFastHessian;
import boofcv.alg.distort.ImageDistort;
import boofcv.alg.distort.PixelTransformHomography_F32;
import boofcv.alg.distort.impl.DistortSupport;
import boofcv.alg.feature.UtilFeature;
import boofcv.alg.interpolate.impl.ImplBilinearPixel_F32;
import boofcv.alg.sfm.robust.DistanceHomographySq;
import boofcv.alg.sfm.robust.GenerateHomographyLinear;
import boofcv.factory.feature.associate.FactoryAssociation;
import boofcv.factory.feature.detdesc.FactoryDetectDescribe;
import boofcv.gui.image.ShowImages;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.feature.AssociatedIndex;
import boofcv.struct.feature.SurfFeature;
import boofcv.struct.feature.TupleDesc;
import boofcv.struct.geo.AssociatedPair;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;
import boofcv.struct.image.MultiSpectral;
import georegression.fitting.homography.ModelManagerHomography2D_F64;
import georegression.struct.homography.Homography2D_F64;
import georegression.struct.point.Point2D_F64;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ddogleg.fitting.modelset.ModelManager;
import org.ddogleg.fitting.modelset.ModelMatcher;
import org.ddogleg.fitting.modelset.ransac.Ransac;
import org.ddogleg.struct.FastQueue;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
/**
 *
 * @author cjramos
 */
public class Stitcher{
    static String newPath;
    static int c;
    static int filter=0;
    
   public Stitcher(){
        System.out.println("Stitcher successfully initialized");
    }
    
    static String initializeImages(String path1, String path2,int counter){ //converts and initializes the input to BufferedImages
        BufferedImage imageA, imageB;
        
        c = counter;
        
        imageA = UtilImageIO.loadImage(path1);
        imageB = UtilImageIO.loadImage(path2);
        newPath = stitch(imageA,imageB,ImageFloat32.class);
        return newPath;             //returns the new path of the created image
    }
    
    public static <T extends ImageSingleBand> 
    String stitch(BufferedImage imageA, BufferedImage imageB, Class<T> imageType){
        T inputA = ConvertBufferedImage.convertFromSingle(imageA, null, imageType);
        T inputB = ConvertBufferedImage.convertFromSingle(imageB, null, imageType);

        // Detect using the standard SURF feature descriptor and describer
        DetectDescribePoint detDesc = FactoryDetectDescribe.surfStable(
                        new ConfigFastHessian(1, 2, 200, 1, 9, 4, 4), null,null, imageType);
        ScoreAssociation<SurfFeature> scorer = FactoryAssociation.scoreEuclidean(SurfFeature.class,true);
        AssociateDescription<SurfFeature> associate = FactoryAssociation.greedy(scorer,2,true);

        // fit the images using a homography.  This works well for rotations and distant objects.
        ModelManager<Homography2D_F64> manager = new ModelManagerHomography2D_F64();
        GenerateHomographyLinear modelFitter = new GenerateHomographyLinear(true);
        DistanceHomographySq distance = new DistanceHomographySq();

        ModelMatcher<Homography2D_F64,AssociatedPair> modelMatcher =
                        new Ransac<Homography2D_F64,AssociatedPair>(123,manager,modelFitter,distance,60,9);

        Homography2D_F64 H = computeTransform(inputA, inputB, detDesc, associate, modelMatcher);

        if(filter==0){  //checks if feature match is appropriate
             newPath = renderStitching(imageA,imageB,H);
             return newPath;
        }
        else{
            JOptionPane.showMessageDialog(null, "Stitching Failed");
            return "Failed";
        }
    }
    
    public static String renderStitching(BufferedImage imageA, BufferedImage imageB,Homography2D_F64 fromAtoB){
        // specify size of output image
        double scale = 0.70;
        int outputWidth = imageA.getWidth();
        int outputHeight = imageA.getHeight();
        String nPath;

        // Convert into a BoofCV color format
        MultiSpectral<ImageFloat32> colorA =
                        ConvertBufferedImage.convertFromMulti(imageA, null,true, ImageFloat32.class);
        MultiSpectral<ImageFloat32> colorB =
                        ConvertBufferedImage.convertFromMulti(imageB, null,true, ImageFloat32.class);

        // Where the output images are rendered into
        MultiSpectral<ImageFloat32> work = new MultiSpectral<ImageFloat32>(ImageFloat32.class,outputWidth,outputHeight,3);

        // Adjust the transform so that the whole image can appear inside of it
        Homography2D_F64 fromAToWork = new Homography2D_F64(scale,0,colorA.width/200,0,scale,colorA.height/200,0,0,1);
        Homography2D_F64 fromWorkToA = fromAToWork.invert(null);

        // Used to render the results onto an image
        PixelTransformHomography_F32 model = new PixelTransformHomography_F32();
        ImageDistort<MultiSpectral<ImageFloat32>,MultiSpectral<ImageFloat32>> distort =
                        DistortSupport.createDistortMS(ImageFloat32.class, model, new ImplBilinearPixel_F32(),false, null);

        // Render first image
        model.set(fromWorkToA);
        distort.apply(colorA,work);

        // Render second image
        Homography2D_F64 fromWorkToB = fromWorkToA.concat(fromAtoB,null);
        model.set(fromWorkToB);
        distort.apply(colorB,work);

        // Convert the rendered image into a BufferedImage
        BufferedImage output = new BufferedImage(work.width,work.height,imageA.getType());
        ConvertBufferedImage.convertTo(work,output,true);

        Graphics2D g2 = output.createGraphics();
        

        try {
            nPath = System.getProperty("user.home") + "/Desktop/Image Stitcher/output_"+c+".jpg";
            ImageIO.write(output, "jpg",new File(nPath));   //outputs the stitched image inside the folder
            System.out.println("Success");
            JOptionPane.showMessageDialog(null, "Successfully Stitched Input Images");
            return nPath;   //returns path of the new image
        } catch (IOException ex) {
            Logger.getLogger(Stitcher.class.getName()).log(Level.SEVERE, null, ex);
            return "Failed";
        }
            

        // draw lines around the distorted image to make it easier to see
        /*Homography2D_F64 fromBtoWork = fromWorkToB.invert(null);
        Point2D_I32 corners[] = new Point2D_I32[4];
        corners[0] = renderPoint(0,0,fromBtoWork);
        corners[1] = renderPoint(colorB.width,0,fromBtoWork);
        corners[2] = renderPoint(colorB.width,colorB.height,fromBtoWork);
        corners[3] = renderPoint(0,colorB.height,fromBtoWork);

        g2.setColor(Color.ORANGE);
        g2.setStroke(new BasicStroke(4));
        g2.drawLine(corners[0].x,corners[0].y,corners[1].x,corners[1].y);
        g2.drawLine(corners[1].x,corners[1].y,corners[2].x,corners[2].y);
        g2.drawLine(corners[2].x,corners[2].y,corners[3].x,corners[3].y);
        g2.drawLine(corners[3].x,corners[3].y,corners[0].x,corners[0].y);*/

       // ShowImages.showWindow(output,"Stitched Images");
    }
    
    public static<T extends ImageSingleBand, FD extends TupleDesc> Homography2D_F64 
    computeTransform(T imageA, T imageB, DetectDescribePoint<T,FD> detDesc, AssociateDescription<FD> associate, ModelMatcher<Homography2D_F64, AssociatedPair> modelMatcher ){
        
        int i;

        // get the length of the description
        List<Point2D_F64> pointsA = new ArrayList<Point2D_F64>();
        FastQueue<FD> descA = UtilFeature.createQueue(detDesc,100);
        List<Point2D_F64> pointsB = new ArrayList<Point2D_F64>();
        FastQueue<FD> descB = UtilFeature.createQueue(detDesc,100);

        // extract feature locations and descriptions from each image
        describeImage(imageA, detDesc, pointsA, descA);
        describeImage(imageB, detDesc, pointsB, descB);

        // Associate features between the two images
        associate.setSource(descA);
        associate.setDestination(descB);
        associate.associate();

        // create a list of AssociatedPairs that tell the model matcher how a feature moved
        FastQueue<AssociatedIndex> matches = associate.getMatches();
        List<AssociatedPair> pairs = new ArrayList<AssociatedPair>();
      
        //Critical Part ------------------------------------------------------------------------------------------!!!!!!
        System.out.println("Matches:"+matches.size());
        if(matches.size()<175){     //Images can't be stitched due to low feature matches
           System.out.println("Low feature match");
           filter = 1;
        }
        else{       //if feature match is above 175
            filter = 0;
        }
        
        for(i=0;i<matches.size();i++){
                AssociatedIndex match = matches.get(i);

                Point2D_F64 a = pointsA.get(match.src);
                Point2D_F64 b = pointsB.get(match.dst);

                pairs.add(new AssociatedPair(a,b,false));
        }

        // find the best fit model to describe the change between these images
        if( !modelMatcher.process(pairs) )
                throw new RuntimeException("Model Matcher failed!");

        // return the found image transform
        return modelMatcher.getModelParameters().copy();
    }
    
    private static <T extends ImageSingleBand, FD extends TupleDesc> void describeImage(T image, DetectDescribePoint<T,FD> detDesc, List<Point2D_F64> points, FastQueue<FD> listDescs){
        int i;

        detDesc.detect(image);

        listDescs.reset();
        for(i=0;i<detDesc.getNumberOfFeatures();i++){
            points.add( detDesc.getLocation(i).copy() );
            listDescs.grow().setTo(detDesc.getDescription(i));
        }
    }
}

