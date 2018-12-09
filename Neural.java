class Main{
    static void main(String[] args){
        // detect the corner case
        if (args.length == 0) {
            return;  // end the program if there is no parameters
        }

        // perform different tasks according to FLAGs
        if (args.length == 12 && args[1].equals("100")) { // flag == 100 is valid
            // leave the fist index empty
            // wn = weights[n];
            Double[] weights = new double[10];  

            // read weights into the array from the paprameters
            for ( int i = 1; i < (9+1); i++) {
                weights[i] = Double.parseDouble(args[i]); 
            }

            // read x1 and x2
            Double x1 = Double.parseDouble(args[11]);
            Double x2 = Double.parseDouble(args[12]);

            Double[] outputs = new Double[6]; //ua, va, ub, vb, uc, vc
            

        }
    }

    /**
     * Calculate u of thenode
     * @param weights in this case, having three elements w1,w2,w3
     * @param vs in this case having three elements v1, v2, v3
     * @return the sum of wi*vi
     */
    static private Double ujHelp(double[] weights, double[] vs) {
        // corner case 
        if (weights.length != vs.length) {
            throw(Exception);
        }

        Double sum = 0; 
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