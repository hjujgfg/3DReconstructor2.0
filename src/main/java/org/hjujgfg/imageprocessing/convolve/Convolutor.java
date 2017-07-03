package org.hjujgfg.imageprocessing.convolve;

import org.apache.log4j.Logger;
import org.hjujgfg.exceptions.FileLoadingException;
import org.hjujgfg.io.FileHelper;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * Created by egor.lapidus on 30/06/2017.
 */
public class Convolutor {

    private final static Logger log = Logger.getLogger(Convolutor.class);

    private FileHelper fileHelper = new FileHelper();

    public BufferedImage convolve(BufferedImage img, Kernel kernel) {
        log.info("convolution started");
        ConvolveOp op = new ConvolveOp(kernel);
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        op.filter(img, result);
        log.info("convolution ended");
        return result;
    }

    public void convolve(String inputImage, String outputImage, String kernelName, Integer size) throws FileLoadingException {
        BufferedImage source = fileHelper.loadImage(inputImage);
        Kernel kernel = KernelFactory.getKernel(Kernels.getByName(kernelName), size);
        BufferedImage res = convolve(source, kernel);
        fileHelper.saveImage(res, outputImage);
    }

}
