package indi.kurok1.analysisByte;

/**
 * 平均数求和，分析字节码
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @version 2021.04.26
 */
public class Average {

    private static final float[] nums = {2,7,9};

    public static void main(String[] args) {
        float a = 20;
        for (float num : nums) {
            a = a + num;
        }
        System.out.printf("result : %f", a / nums.length);
    }

}
