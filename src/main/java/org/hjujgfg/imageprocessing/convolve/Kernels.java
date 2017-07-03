package org.hjujgfg.imageprocessing.convolve;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by egor.lapidus on 01/07/2017.
 */
public enum Kernels {

    OUTLINE("outline"),
    EMBOSS("emboss"),
    SHARPEN("sharpen"),
    X_GRADIENT("xgrad"),
    Y_GRADIENT("ygrad"),
    XX_LOG("xxlog"),
    YY_LOG("yylog"),
    XY_LOG("xylog");

    private String name;

    Kernels(String name) {
        this.name = name;
    }

    public static Kernels getByName(String name) {
        Optional<Kernels> res = Arrays.stream(Kernels.values())
                .filter(k -> k.name.equals(name))
                .findFirst();
        if (res.isPresent()){
            return res.get();
        }
        throw new IllegalArgumentException("Wrong kernel name");
    }
}
