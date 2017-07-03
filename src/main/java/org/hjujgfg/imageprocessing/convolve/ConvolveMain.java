package org.hjujgfg.imageprocessing.convolve;

import org.hjujgfg.consts.ImageConsts;
import org.hjujgfg.imageprocessing.ImageProcessor;
import org.hjujgfg.io.DirectoryHelper;
import org.hjujgfg.io.FileHelper;

import java.awt.image.BufferedImage;

/**
 * Created by egor.lapidus on 30/06/2017.
 */
public class ConvolveMain {


    public static void main(String[] args) throws Exception {
        String model = "sheep";
        DirectoryHelper directoryHelper = new DirectoryHelper();
        directoryHelper.createDefaultDirs();
        directoryHelper.clearTemporalImages();
        FileHelper fileHelper = new FileHelper();
        fileHelper.prepareFilesByModel(model);
        Convolutor convolutor = new Convolutor();
        BufferedImage img = fileHelper.tryGetCurrentModelByCounter(model, 0);
        ImageProcessor processor = new ImageProcessor();
        img = processor.toGrayScale(img);
        fileHelper.saveImage(img, String.format("%s%s.png",ImageConsts.CONVOLVE_OUT, "mainTestGray"));
        BufferedImage img2 = convolutor.convolve(img, KernelFactory.buildXYGaussianKernel(9));
        fileHelper.saveImage(img2, String.format("%s%s.png",ImageConsts.CONVOLVE_OUT, "SingleFilter"));
        img = convolutor.convolve(img, KernelFactory.buildEmbossKernel());
        fileHelper.saveImage(img, String.format("%s%s.png",ImageConsts.CONVOLVE_OUT, "mainTestEmboss"));
        img = convolutor.convolve(img, KernelFactory.buildXYGaussianKernel(9));
        fileHelper.saveImage(img, String.format("%s%s.png",ImageConsts.CONVOLVE_OUT, "mainTestGauss"));
    }
}
