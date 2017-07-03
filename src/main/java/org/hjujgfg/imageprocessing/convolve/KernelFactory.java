package org.hjujgfg.imageprocessing.convolve;

import org.apache.log4j.Logger;
import org.hjujgfg.consts.ImageConsts;
import org.hjujgfg.io.FileHelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Kernel;

/**
 * Created by egor.lapidus on 30/06/2017.
 */
public class KernelFactory {

    private final static Logger log = Logger.getLogger(KernelFactory.class);

    public enum ApproximationSize {
        NINE, FIFTEEN, TWENTY_ONE, TWENTY_SEVEN
    }

    public static Kernel getKernel(Kernels kernel) {
        switch (kernel) {
            case EMBOSS: return buildEmbossKernel();
            case OUTLINE: return buildOutlineKernel();
            case SHARPEN: return buildSharpenKernel();
            case X_GRADIENT: return buildXSimpleGradientKernel();
            case Y_GRADIENT: return buildYSimpleGradientKernel();
            default:
                log.error("Wrong kernel chosen");
                throw new IllegalArgumentException("Wrong kernel");
        }
    }

    public static Kernel getKernel(Kernels kernel, Integer size) {
        switch (kernel) {
            case EMBOSS: return buildEmbossKernel();
            case OUTLINE: return buildOutlineKernel();
            case SHARPEN: return buildSharpenKernel();
            case X_GRADIENT: return buildXSimpleGradientKernel();
            case Y_GRADIENT: return buildYSimpleGradientKernel();
            case XX_LOG:
                if (size != null)
                    return buildXXGaussianKernel(size);
            case YY_LOG:
                if (size != null)
                    return buildYYGaussianKernel(size);
            case XY_LOG:
                if (size != null)
                    return buildXYGaussianKernel(size);
            default:
                log.error("Wrong kernel chosen");
                throw new IllegalArgumentException("Wrong kernel");
        }
    }



    public static Kernel buildOutlineKernel() {
        float [] k = new float[9];
        int counter = 0;
        for (int i = 0; i < 9; i ++) {
            k[i] = -1;
        }
        k [5] = 8;
        return new Kernel(3, 3, k);
    }

    public static Kernel buildEmbossKernel() {
        float[] k = new float[9];
        k[0] = -2;
        k[1] = -1;
        k[2] = 0;
        k[3] = -1;
        k[4] = 1;
        k[5] = 1;
        k[6] = 0;
        k[7] = 1;
        k[8] = 2;
        return new Kernel(3, 3, k);
    }

    public static Kernel buildSharpenKernel() {
        float [] k = new float[9];
        k[0] = 0;
        k[1] = -1;
        k[2] = 0;
        k[3] = -1;
        k[4] = 5;
        k[5] = -1;
        k[6] = 0;
        k[7] = -1;
        k[8] = 0;
        return new Kernel(3, 3, k);
    }

    /*public static Kernel buildGaussianSmoothkernel() {
        float [] k = new float[9 * 9];
        for (int i = 0; i < 9 * 9; i++) {

        }
    }*/

    public static Kernel buildXYGaussianKernel(int size) {
        int squareSize = 3;
        int borderWidth = 1;

        float [] k = new float[size * size];
        switch (size) {
            case 9: squareSize = 3; borderWidth = 1; break;
            case 15: squareSize = 5; borderWidth = 2; break;
            case 21: squareSize = 7; borderWidth = 3; break;
        }
        int counter = 0;
        for (int i = 0; i < size; i ++) {
            for (int j = 0; j < size; j++) {
                if (i < borderWidth || (size - borderWidth) <= i
                        || j < borderWidth || (size - borderWidth) <= j
                        || i == (size / 2) || j == (size / 2)) {
                    k[counter++] = 0;
                    continue;
                }
                if (j >= borderWidth && j < (size / 2)) {
                    if (i < size / 2) {
                        k[counter++] = 1;
                    } else {
                        k[counter++] = -1;
                    }
                } else {
                    if (i < size / 2) {
                        k[counter++] = -1;
                    } else {
                        k[counter++] = 1;
                    }
                }
            }
        }
                /*if (i == 0 || i == 8 || i == 4
                    || j == 0 || j == 4 || j == 8) {
                    k[counter++] = 0;
                    continue;
                }
                if (Math.abs(i - j) <= 2) {
                    k[counter++] = 1;
                    continue;
                }
                k[counter ++] = -1;
            }
        }
        k[9 * 3 + 5] = -1;
        k[9 * 5 + 3] = -1;*/
        //visualizeKernel(k, size);
        return new Kernel(size, size, k);
    }

    public static Kernel buildXXGaussianKernel(int side) {
        float [] k = new float[side * side];
        int borderSize = 2;
        switch (side) {
            case 9 : borderSize = 2; break;
            case 15: borderSize = 3; break;
            case 21: borderSize = 4; break;
            case 27: borderSize = 5; break;
        }
        int counter = 0;
        for (int i = 0; i < side; i ++) {
            for (int j = 0; j < side; j ++) {
                if (j < borderSize || j >= (side - borderSize)) {
                    k[counter ++ ] = 0;
                    continue;
                }
                if (i >= (side / 3) && i < 2 * (side / 3)) {
                    k[counter ++] = -2;
                    continue;
                }
                k[counter ++] = 1;
            }
        }
        //visualizeKernel(k, side);
        return new Kernel(side, side, k);
    }

    public static Kernel buildYYGaussianKernel(int side) {
        float [] k = new float[side * side];
        int borderSize = 2;
        switch (side) {
            case 9 : borderSize = 2; break;
            case 15: borderSize = 3; break;
            case 21: borderSize = 4; break;
            case 27: borderSize = 5; break;
        }
        int counter = 0;
        for (int i = 0; i < side; i ++) {
            for (int j = 0; j < side; j ++) {
                if (i < borderSize || i >= (side - borderSize)) {
                    k[counter ++ ] = 0;
                    continue;
                }
                if (j >= (side / 3) && j < 2 * (side / 3)) {
                    k[counter ++] = -2;
                    continue;
                }
                k[counter ++] = 1;
            }
        }
        //visualizeKernel(k, side);
        return new Kernel(side, side, k);
    }

    public static Kernel buildYSimpleGradientKernel() {
        return new Kernel(1, 3, new float[] {-1.f, 0.f, 1.f});
    }

    public static Kernel buildXSimpleGradientKernel() {
        return new Kernel(3, 1, new float[] {-1.f, 0.f, 1.f});
    }

    public static void visualizeKernel(float[] k, int dim) {
        BufferedImage img = new BufferedImage(dim, dim, BufferedImage.TYPE_BYTE_GRAY);
        int counter = 0;
        for (int i = 0; i < dim; i ++) {
            for (int j = 0; j < dim; j ++) {
                Color color;
                if (k[counter] == 0) {
                    color = Color.lightGray;
                } else if (k[counter] > 0) {
                    color = Color.white;
                } else color = Color.BLACK;
                img.setRGB(j, i, color.getRGB());
                counter ++;
            }
        }
        FileHelper fHelper = new FileHelper();
        fHelper.saveImage(img, ImageConsts.CONVOLVE_OUT + "box" + anInt++ + ".png");
    }
    static int anInt = 0;
}
