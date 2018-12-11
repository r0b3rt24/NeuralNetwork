import sun.jvm.hotspot.runtime.VM;

import java.util.Arrays;

class Neural{
    public static void main(String[] args){
        // detect the corner case
        if (args.length == 0) {
            return;  // end the program if there is no parameters
        }

        // perform different tasks according to FLAGs
        if (args.length == 12 && args[0].equals("100")) { // flag == 100 is valid
            Double[] weights = readWeights(args);

            // read x1 and x2
            Double x1 = Double.parseDouble(args[10]);
            Double x2 = Double.parseDouble(args[11]);

            Double[] outputs = Flag100Function(weights, x1, x2); // ua, va, ub, vb, uc, vc
            printer(outputs);

        } else if (args.length == 13 && args[0].equals("200")){
            Double[] weights = readWeights(args);

            // read x1 and x2
            Double x1 = Double.parseDouble(args[10]);
            Double x2 = Double.parseDouble(args[11]);
            Double y = Double.parseDouble(args[12]);

            // ua, va, ub, vb, uc, vc
            Double[] FLAG100OP = Flag100Function(weights, x1, x2);
            Double[] OP = Flag200Function(FLAG100OP[5], y);
            printer(OP);

        } else if (args.length == 13 && args[0].equals("300")) {
            Double[] weights = readWeights(args);

            // read x1 and x2
            Double x1 = Double.parseDouble(args[10]);
            Double x2 = Double.parseDouble(args[11]);
            Double y = Double.parseDouble(args[12]);

            // ua, va, ub, vb, uc, vc
            Double[] FLAG100OP = Flag100Function(weights, x1, x2);
            Double[] FLAG200OP = Flag200Function(FLAG100OP[5], y);

            Double[] FLAG300OP = Flag300Function(weights, FLAG100OP, FLAG200OP);
            printer(FLAG300OP);

        } else {
            System.out.println("Read the PDF for usage");
        }
    }

    static private Double[] Flag300Function(Double[] w, Double[] FLAG100OP, Double[] FLAG200OP) {
        Double[] res = new Double[4];  //Va' Ua' Vb' Ub'
        res[0] = partialHiddenVj(w[8], FLAG200OP[2]);
        res[1] = partialHiddenUj(res[0], derivativeReLu(FLAG100OP[0]));
        res[2] = partialHiddenVj(w[9], FLAG200OP[2]);
        res[3] = partialHiddenUj(res[2], derivativeReLu(FLAG100OP[2]));
        return res;
    }

    static private Double partialHiddenVj(Double weights, Double partialUk) {
//        Double sum = 0.0;
//        for (Double d : weights) {
//            sum += d * partialUk;
//        }
        return weights * partialUk;
    }

    static private Double partialHiddenUj(Double partialVj, Double derivativeReLU) {
        return partialVj * derivativeReLU;
    }

    static private Double derivativeReLu(Double Uj) {
        if(Uj >= 0) {
            return 1.0;
        } else {
            return 0.0;
        }
    }


    /**
     *
     * @param Vc
     * @param y
     * @return
     */
    static private Double[] Flag200Function(Double Vc, Double y) {
        Double[] OP = new Double[3];  // E partialVc partialUc
        OP[0] = getError(Vc, y);
        OP[1] = partialVc(Vc, y);
        OP[2] = partialUc(Vc, y);
        return OP;
    }

    /**
     *
     * @param weights
     * @param x1
     * @param x2
     * @return
     */
    static private Double[] Flag100Function(Double[] weights, Double x1, Double x2){
        Double[] outputs = new Double[6]; //ua, va, ub, vb, uc, vc

        outputs[0] = ujHelp(Arrays.copyOfRange(weights, 1, 4), new Double[]{1.0, x1, x2});  // ua
        outputs[2] = ujHelp(Arrays.copyOfRange(weights, 4, 7), new Double[]{1.0, x1, x2});  // ub

        outputs[1] = ReLuHelper(outputs[0]); // va
        outputs[3] = ReLuHelper(outputs[2]); // vb

        outputs[4] = ujHelp(Arrays.copyOfRange(weights, 7, 10), new Double[]{1.0, outputs[1], outputs[3]}); // uc
        outputs[5] = signoidHelper(outputs[4]);  // vc

        return outputs;
    }

    /**
     *
     * @param Vc
     * @param y
     * @return
     */
    static private Double partialVc(Double Vc, Double y) {
        return Vc - y;
    }

    /**
     *
     * @param Vc
     * @param y
     * @return
     */
    static private Double partialUc(Double Vc, Double y) {
        return (Vc - y) * Vc * (1 - Vc);
    }

    /**
     *
     * @param Vc
     * @param y
     * @return
     */
    static private Double getError(Double Vc, Double y) {
        return 0.5 * (Math.pow((Vc - y), 2));
    }

    /**
     *
     * @param args
     * @return
     */
    static private Double[] readWeights(String[] args) {
        // leave the fist index empty
        // wn = weights[n];
        Double[] weights = new Double[10];

        // read weights into the array from the paprameters
        for ( int i = 1; i < (9+1); i++) {
            weights[i] = Double.parseDouble(args[i]);
        }
        return weights;
    }

    /**
     * Helping Flag 100 to print the result
     * @param arr
     */
    static private void printer(Double[] arr) {
        for (int i = 0; i < arr.length; i++) {
            String out =String.format("%.5f", arr[i]);
            System.out.print(out+" ");
        }
    }

    /**
     * Calculate u of thenode
     * @param weights in this case, having three elements w1,w2,w3
     * @param vs in this case having three elements v1, v2, v3
     * @return the sum of wi*vi
     */
    static private Double ujHelp(Double[] weights, Double[] vs) {
        // corner case 
        if (weights.length != vs.length) {
            return null;
        }

        Double sum = 0.0;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i] * vs[i];
        }
        return sum;
    }

    /**
     * Signoid's f(z) function
     * @param z the input z
     * @return the f(z)
     */
    static private Double signoidHelper(Double z) {
        return 1/(1 + Math.pow(Math.E, -z));
    }

    /**
     *  ReLU's f(z) function
     * @param z the input z
     * @return the f(z)
     */
    static private Double ReLuHelper(Double z) {
        return Math.max(0, z);
    }
}