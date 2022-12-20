package xyz.acrylicstyle.util.serialization.app;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.serialization.codec.Codec;
import xyz.acrylicstyle.util.serialization.codec.MapCodec;
import xyz.acrylicstyle.util.serialization.constructors.CodecConstructor0;

public final class App0<A> {
    private static final App0<?> EMPTY = new App0<>();

    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <A> @NotNull App0<A> getInstance() {
        return (App0<A>) EMPTY;
    }

    @Contract(pure = true)
    private App0() {}

    @Contract(value = "_ -> new", pure = true)
    public <P1> @NotNull App1<A, P1> group(@NotNull MapCodec<P1, A> p1) {
        return new App1<>(p1);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public <P1, P2> @NotNull App2<A, P1, P2> group(
            @NotNull MapCodec<P1, A> p1,
            @NotNull MapCodec<P2, A> p2
    ) {
        return new App2<>(p1, p2);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public <P1, P2, P3> @NotNull App3<A, P1, P2, P3> group(
            @NotNull MapCodec<P1, A> p1,
            @NotNull MapCodec<P2, A> p2,
            @NotNull MapCodec<P3, A> p3
    ) {
        return new App3<>(p1, p2, p3);
    }

    @Contract(value = "_, _, _, _ -> new", pure = true)
    public <P1, P2, P3, P4> @NotNull App4<A, P1, P2, P3, P4> group(
            @NotNull MapCodec<P1, A> p1,
            @NotNull MapCodec<P2, A> p2,
            @NotNull MapCodec<P3, A> p3,
            @NotNull MapCodec<P4, A> p4
    ) {
        return new App4<>(p1, p2, p3, p4);
    }

    @Contract(value = "_, _, _, _, _ -> new", pure = true)
    public <P1, P2, P3, P4, P5> @NotNull App5<A, P1, P2, P3, P4, P5> group(
            @NotNull MapCodec<P1, A> p1,
            @NotNull MapCodec<P2, A> p2,
            @NotNull MapCodec<P3, A> p3,
            @NotNull MapCodec<P4, A> p4,
            @NotNull MapCodec<P5, A> p5
    ) {
        return new App5<>(p1, p2, p3, p4, p5);
    }

    @Contract(value = "_, _, _, _, _, _ -> new", pure = true)
    public <P1, P2, P3, P4, P5, P6> @NotNull App6<A, P1, P2, P3, P4, P5, P6> group(
            @NotNull MapCodec<P1, A> p1,
            @NotNull MapCodec<P2, A> p2,
            @NotNull MapCodec<P3, A> p3,
            @NotNull MapCodec<P4, A> p4,
            @NotNull MapCodec<P5, A> p5,
            @NotNull MapCodec<P6, A> p6
    ) {
        return new App6<>(p1, p2, p3, p4, p5, p6);
    }

    @Contract(value = "_, _, _, _, _, _, _ -> new", pure = true)
    public <P1, P2, P3, P4, P5, P6, P7> @NotNull App7<A, P1, P2, P3, P4, P5, P6, P7> group(
            @NotNull MapCodec<P1, A> p1,
            @NotNull MapCodec<P2, A> p2,
            @NotNull MapCodec<P3, A> p3,
            @NotNull MapCodec<P4, A> p4,
            @NotNull MapCodec<P5, A> p5,
            @NotNull MapCodec<P6, A> p6,
            @NotNull MapCodec<P7, A> p7
    ) {
        return new App7<>(p1, p2, p3, p4, p5, p6, p7);
    }

    @Contract(value = "_, _, _, _, _, _, _, _ -> new", pure = true)
    public <P1, P2, P3, P4, P5, P6, P7, P8> @NotNull App8<A, P1, P2, P3, P4, P5, P6, P7, P8> group(
            @NotNull MapCodec<P1, A> p1,
            @NotNull MapCodec<P2, A> p2,
            @NotNull MapCodec<P3, A> p3,
            @NotNull MapCodec<P4, A> p4,
            @NotNull MapCodec<P5, A> p5,
            @NotNull MapCodec<P6, A> p6,
            @NotNull MapCodec<P7, A> p7,
            @NotNull MapCodec<P8, A> p8
    ) {
        return new App8<>(p1, p2, p3, p4, p5, p6, p7, p8);
    }

    @Contract(value = "_, _, _, _, _, _, _, _, _ -> new", pure = true)
    public <P1, P2, P3, P4, P5, P6, P7, P8, P9> @NotNull App9<A, P1, P2, P3, P4, P5, P6, P7, P8, P9> group(
            @NotNull MapCodec<P1, A> p1,
            @NotNull MapCodec<P2, A> p2,
            @NotNull MapCodec<P3, A> p3,
            @NotNull MapCodec<P4, A> p4,
            @NotNull MapCodec<P5, A> p5,
            @NotNull MapCodec<P6, A> p6,
            @NotNull MapCodec<P7, A> p7,
            @NotNull MapCodec<P8, A> p8,
            @NotNull MapCodec<P9, A> p9
    ) {
        return new App9<>(p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    @Contract(value = "_, _, _, _, _, _, _, _, _, _ -> new", pure = true)
    public <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> @NotNull App10<A, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> group(
            @NotNull MapCodec<P1, A> p1,
            @NotNull MapCodec<P2, A> p2,
            @NotNull MapCodec<P3, A> p3,
            @NotNull MapCodec<P4, A> p4,
            @NotNull MapCodec<P5, A> p5,
            @NotNull MapCodec<P6, A> p6,
            @NotNull MapCodec<P7, A> p7,
            @NotNull MapCodec<P8, A> p8,
            @NotNull MapCodec<P9, A> p9,
            @NotNull MapCodec<P10, A> p10
    ) {
        return new App10<>(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
    }

    @Contract(value = "_, _, _, _, _, _, _, _, _, _, _ -> new", pure = true)
    public <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11> @NotNull
            App11<A, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11> group(
            @NotNull MapCodec<P1, A> p1,
            @NotNull MapCodec<P2, A> p2,
            @NotNull MapCodec<P3, A> p3,
            @NotNull MapCodec<P4, A> p4,
            @NotNull MapCodec<P5, A> p5,
            @NotNull MapCodec<P6, A> p6,
            @NotNull MapCodec<P7, A> p7,
            @NotNull MapCodec<P8, A> p8,
            @NotNull MapCodec<P9, A> p9,
            @NotNull MapCodec<P10, A> p10,
            @NotNull MapCodec<P11, A> p11
    ) {
        return new App11<>(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11);
    }

    @Contract(value = "_, _, _, _, _, _, _, _, _, _, _, _ -> new", pure = true)
    public <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12> @NotNull
            App12<A, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12> group(
            @NotNull MapCodec<P1, A> p1,
            @NotNull MapCodec<P2, A> p2,
            @NotNull MapCodec<P3, A> p3,
            @NotNull MapCodec<P4, A> p4,
            @NotNull MapCodec<P5, A> p5,
            @NotNull MapCodec<P6, A> p6,
            @NotNull MapCodec<P7, A> p7,
            @NotNull MapCodec<P8, A> p8,
            @NotNull MapCodec<P9, A> p9,
            @NotNull MapCodec<P10, A> p10,
            @NotNull MapCodec<P11, A> p11,
            @NotNull MapCodec<P12, A> p12
    ) {
        return new App12<>(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12);
    }

    @Contract(value = "_, _, _, _, _, _, _, _, _, _, _, _, _ -> new", pure = true)
    public <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13> @NotNull
            App13<A, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13> group(
            @NotNull MapCodec<P1, A> p1,
            @NotNull MapCodec<P2, A> p2,
            @NotNull MapCodec<P3, A> p3,
            @NotNull MapCodec<P4, A> p4,
            @NotNull MapCodec<P5, A> p5,
            @NotNull MapCodec<P6, A> p6,
            @NotNull MapCodec<P7, A> p7,
            @NotNull MapCodec<P8, A> p8,
            @NotNull MapCodec<P9, A> p9,
            @NotNull MapCodec<P10, A> p10,
            @NotNull MapCodec<P11, A> p11,
            @NotNull MapCodec<P12, A> p12,
            @NotNull MapCodec<P13, A> p13
    ) {
        return new App13<>(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13);
    }

    @Contract(value = "_, _, _, _, _, _, _, _, _, _, _, _, _, _ -> new", pure = true)
    public <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14> @NotNull
            App14<A, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14> group(
            @NotNull MapCodec<P1, A> p1,
            @NotNull MapCodec<P2, A> p2,
            @NotNull MapCodec<P3, A> p3,
            @NotNull MapCodec<P4, A> p4,
            @NotNull MapCodec<P5, A> p5,
            @NotNull MapCodec<P6, A> p6,
            @NotNull MapCodec<P7, A> p7,
            @NotNull MapCodec<P8, A> p8,
            @NotNull MapCodec<P9, A> p9,
            @NotNull MapCodec<P10, A> p10,
            @NotNull MapCodec<P11, A> p11,
            @NotNull MapCodec<P12, A> p12,
            @NotNull MapCodec<P13, A> p13,
            @NotNull MapCodec<P14, A> p14
    ) {
        return new App14<>(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14);
    }

    @Contract(value = "_, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> new", pure = true)
    public <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15> @NotNull
            App15<A, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15> group(
            @NotNull MapCodec<P1, A> p1,
            @NotNull MapCodec<P2, A> p2,
            @NotNull MapCodec<P3, A> p3,
            @NotNull MapCodec<P4, A> p4,
            @NotNull MapCodec<P5, A> p5,
            @NotNull MapCodec<P6, A> p6,
            @NotNull MapCodec<P7, A> p7,
            @NotNull MapCodec<P8, A> p8,
            @NotNull MapCodec<P9, A> p9,
            @NotNull MapCodec<P10, A> p10,
            @NotNull MapCodec<P11, A> p11,
            @NotNull MapCodec<P12, A> p12,
            @NotNull MapCodec<P13, A> p13,
            @NotNull MapCodec<P14, A> p14,
            @NotNull MapCodec<P15, A> p15
    ) {
        return new App15<>(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15);
    }

    @Contract(value = "_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> new", pure = true)
    public <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16> @NotNull
            App16<A, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16> group(
            @NotNull MapCodec<P1, A> p1,
            @NotNull MapCodec<P2, A> p2,
            @NotNull MapCodec<P3, A> p3,
            @NotNull MapCodec<P4, A> p4,
            @NotNull MapCodec<P5, A> p5,
            @NotNull MapCodec<P6, A> p6,
            @NotNull MapCodec<P7, A> p7,
            @NotNull MapCodec<P8, A> p8,
            @NotNull MapCodec<P9, A> p9,
            @NotNull MapCodec<P10, A> p10,
            @NotNull MapCodec<P11, A> p11,
            @NotNull MapCodec<P12, A> p12,
            @NotNull MapCodec<P13, A> p13,
            @NotNull MapCodec<P14, A> p14,
            @NotNull MapCodec<P15, A> p15,
            @NotNull MapCodec<P16, A> p16
    ) {
        return new App16<>(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16);
    }

    @Contract(value = "_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> new", pure = true)
    public <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17> @NotNull
            App17<A, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17> group(
            @NotNull MapCodec<P1, A> p1,
            @NotNull MapCodec<P2, A> p2,
            @NotNull MapCodec<P3, A> p3,
            @NotNull MapCodec<P4, A> p4,
            @NotNull MapCodec<P5, A> p5,
            @NotNull MapCodec<P6, A> p6,
            @NotNull MapCodec<P7, A> p7,
            @NotNull MapCodec<P8, A> p8,
            @NotNull MapCodec<P9, A> p9,
            @NotNull MapCodec<P10, A> p10,
            @NotNull MapCodec<P11, A> p11,
            @NotNull MapCodec<P12, A> p12,
            @NotNull MapCodec<P13, A> p13,
            @NotNull MapCodec<P14, A> p14,
            @NotNull MapCodec<P15, A> p15,
            @NotNull MapCodec<P16, A> p16,
            @NotNull MapCodec<P17, A> p17
    ) {
        return new App17<>(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17);
    }

    @Contract(value = "_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> new", pure = true)
    public <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18> @NotNull
            App18<A, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18> group(
            @NotNull MapCodec<P1, A> p1,
            @NotNull MapCodec<P2, A> p2,
            @NotNull MapCodec<P3, A> p3,
            @NotNull MapCodec<P4, A> p4,
            @NotNull MapCodec<P5, A> p5,
            @NotNull MapCodec<P6, A> p6,
            @NotNull MapCodec<P7, A> p7,
            @NotNull MapCodec<P8, A> p8,
            @NotNull MapCodec<P9, A> p9,
            @NotNull MapCodec<P10, A> p10,
            @NotNull MapCodec<P11, A> p11,
            @NotNull MapCodec<P12, A> p12,
            @NotNull MapCodec<P13, A> p13,
            @NotNull MapCodec<P14, A> p14,
            @NotNull MapCodec<P15, A> p15,
            @NotNull MapCodec<P16, A> p16,
            @NotNull MapCodec<P17, A> p17,
            @NotNull MapCodec<P18, A> p18
    ) {
        return new App18<>(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18);
    }

    @Contract(value = "_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> new", pure = true)
    public <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19> @NotNull
            App19<A, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19> group(
            @NotNull MapCodec<P1, A> p1,
            @NotNull MapCodec<P2, A> p2,
            @NotNull MapCodec<P3, A> p3,
            @NotNull MapCodec<P4, A> p4,
            @NotNull MapCodec<P5, A> p5,
            @NotNull MapCodec<P6, A> p6,
            @NotNull MapCodec<P7, A> p7,
            @NotNull MapCodec<P8, A> p8,
            @NotNull MapCodec<P9, A> p9,
            @NotNull MapCodec<P10, A> p10,
            @NotNull MapCodec<P11, A> p11,
            @NotNull MapCodec<P12, A> p12,
            @NotNull MapCodec<P13, A> p13,
            @NotNull MapCodec<P14, A> p14,
            @NotNull MapCodec<P15, A> p15,
            @NotNull MapCodec<P16, A> p16,
            @NotNull MapCodec<P17, A> p17,
            @NotNull MapCodec<P18, A> p18,
            @NotNull MapCodec<P19, A> p19
    ) {
        return new App19<>(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19);
    }

    @Contract(value = "_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> new", pure = true)
    public <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20> @NotNull
            App20<A, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20> group(
            @NotNull MapCodec<P1, A> p1,
            @NotNull MapCodec<P2, A> p2,
            @NotNull MapCodec<P3, A> p3,
            @NotNull MapCodec<P4, A> p4,
            @NotNull MapCodec<P5, A> p5,
            @NotNull MapCodec<P6, A> p6,
            @NotNull MapCodec<P7, A> p7,
            @NotNull MapCodec<P8, A> p8,
            @NotNull MapCodec<P9, A> p9,
            @NotNull MapCodec<P10, A> p10,
            @NotNull MapCodec<P11, A> p11,
            @NotNull MapCodec<P12, A> p12,
            @NotNull MapCodec<P13, A> p13,
            @NotNull MapCodec<P14, A> p14,
            @NotNull MapCodec<P15, A> p15,
            @NotNull MapCodec<P16, A> p16,
            @NotNull MapCodec<P17, A> p17,
            @NotNull MapCodec<P18, A> p18,
            @NotNull MapCodec<P19, A> p19,
            @NotNull MapCodec<P20, A> p20
    ) {
        return new App20<>(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20);
    }

    public @NotNull Codec<A> build(@NotNull CodecConstructor0<A> constructor) {
        return new GenericCodec0<>(constructor);
    }
}
