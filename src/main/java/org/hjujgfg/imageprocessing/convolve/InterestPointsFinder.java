package org.hjujgfg.imageprocessing.convolve;

import java.awt.image.BufferedImage;
import java.awt.image.Kernel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by egor.lapidus on 02/07/2017.
 */
public class InterestPointsFinder {

    private Convolutor convolutor = new Convolutor();

    public void findInterestPoints(BufferedImage img) {

    }


    private List<BufferedImage> applyGaussianFilters(BufferedImage img, int size) {
        List<BufferedImage> res = new ArrayList<>(3);
        int kernelSize = 15;
        Kernel[] kernels = {
                KernelFactory.getKernel(Kernels.XX_LOG, kernelSize),
                KernelFactory.getKernel(Kernels.YY_LOG, kernelSize),
                KernelFactory.getKernel(Kernels.XY_LOG, kernelSize)
        };


        for (int i = 0; i < 3; i++) {
            /*BufferedImage applied = convolutor.convolve(img, kernels[i]);
            *//*res.add(tmp);*//*
            NonMaxSuppression suppression = new NonMaxSuppression();
            int [] imgArr = processor.grayToIntArray(tmp);
            suppression.init(imgArr, img.getWidth(), img.getHeight());
            int[] resArr = suppression.process();
            BufferedImage suppressed = processor.intArrToImg(resArr, img.getWidth(), img.getHeight());
            processor.saveImage(suppressed, DirectoryHelper.CONVOLVE_OUT + "suppressed" + i + "" + FILTER_SIZE + ".png");
            res.add(suppressed);*/
        }
        return res;
    }
}
