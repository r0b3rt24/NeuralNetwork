import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


class Neural{

    private static class Student {
        Double midterm;
        Double HW;
        Double A;

        Student(String hw, String mid, String a) {
            midterm = Double.parseDouble(mid);
            HW = Double.parseDouble(hw);
            A = Double.parseDouble(a);
        }
    }

    private static ArrayList<Student> fileReader(int FLAG) {
        String trainFileName = "./hw2_midterm_A_train.txt";
        String testFileName = "./hw2_midterm_A_test.txt";
        String evalFileName = "./hw2_midterm_A_eval.txt";
        String fileName;
        if (FLAG == 100) {
            fileName = trainFileName;
        } else if (FLAG == 200) {
            fileName = evalFileName;
        } else {
            fileName = testFileName;
        }

        ArrayList<Student> res = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(fileName));
            while (scanner.hasNextLine()) {
                String[] s = scanner.nextLine().split(" ");
                Student student = new Student(s[0], s[1], s[2]);
                res.add(student);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }


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

        } else if (args.length == 13 && args[0].equals("400")) {
            Double[] weights = readWeights(args);

            // read x1 and x2
            Double x1 = Double.parseDouble(args[10]);
            Double x2 = Double.parseDouble(args[11]);
            Double y = Double.parseDouble(args[12]);

            Double[] FLAG100OP = Flag100Function(weights, x1, x2);  // ua, va, ub, vb, uc, vc
            Double[] FLAG200OP = Flag200Function(FLAG100OP[5], y);  // E, Vc', Uc'
            Double[] FLAG300OP = Flag300Function(weights, FLAG100OP, FLAG200OP);  // Va' Ua' Vb' Ub'

            Double[] FLAG400OP = FLag400Function(FLAG100OP, FLAG200OP, FLAG300OP, x1, x2);
            printer(FLAG400OP);
        } else if (args.length == 14 && args[0].equals("500")) {
            Double[] weights = readWeights(args);

            // read x1 and x2
            Double x1 = Double.parseDouble(args[10]);
            Double x2 = Double.parseDouble(args[11]);
            Double y = Double.parseDouble(args[12]);
            Double n = Double.parseDouble(args[13]);

            Double[] FLAG100OP = Flag100Function(weights, x1, x2);  // ua, va, ub, vb, uc, vc
            Double[] FLAG200OP = Flag200Function(FLAG100OP[5], y);  // E, Vc', Uc'
            Double[] FLAG300OP = Flag300Function(weights, FLAG100OP, FLAG200OP);  // Va' Ua' Vb' Ub'
            Double[] FLAG400OP = FLag400Function(FLAG100OP, FLAG200OP, FLAG300OP, x1, x2);  // w1' w2' ... w9'
            Double[] FLAG500OP = newWs(n, weights, FLAG400OP);  // w1' w2' ... w9'
            weightsPrinter(weights);
            formatPrinter(FLAG200OP[0]);
            weights = FLAG500OP;

            weightsPrinter(weights);
            FLAG100OP = Flag100Function(weights, x1, x2);  // ua, va, ub, vb, uc, vc
            FLAG200OP = Flag200Function(FLAG100OP[5], y);  // E, Vc', Uc'
            formatPrinter(FLAG200OP[0]);
        } else if (args.length == 11 && args[0].equals("600")) {
            Double[] weights = readWeights(args);

            // read x1 and x2
            Double n = Double.parseDouble(args[10]);
            ArrayList<Student> students = fileReader(100);

            for (Student s : students) {
                Double x1 = s.HW;
                Double x2 = s.midterm;
                Double y = s.A;

                Double[] sinfo = new Double[]{x1, x2, y};

                Double[] FLAG100OP = Flag100Function(weights, x1, x2);  // ua, va, ub, vb, uc, vc
                Double[] FLAG200OP = Flag200Function(FLAG100OP[5], y);  // E, Vc', Uc'
                Double[] FLAG300OP = Flag300Function(weights, FLAG100OP, FLAG200OP);  // Va' Ua' Vb' Ub'
                Double[] FLAG400OP = FLag400Function(FLAG100OP, FLAG200OP, FLAG300OP, x1, x2);  // w1' w2' ... w9'
                Double[] FLAG500OP = newWs(n, weights, FLAG400OP);  // w1' w2' ... w9'
                printer(sinfo);
                weights = FLAG500OP;
                weightsPrinter(weights);
                formatPrinter(evaluationSetE(weights));
            }
        } else if (args.length == 12 && args[0].equals("700")) {
            Double[] weights = readWeights(args);

            // read x1 and x2
            Double n = Double.parseDouble(args[10]);
            ArrayList<Student> students = fileReader(100);

            int T = Integer.parseInt(args[11]);
            for (int i = 0; i < T; i++) {
                for (Student s : students) {
                    Double x1 = s.HW;
                    Double x2 = s.midterm;
                    Double y = s.A;

                    Double[] sinfo = new Double[]{x1, x2, y};

                    Double[] FLAG100OP = Flag100Function(weights, x1, x2);  // ua, va, ub, vb, uc, vc
                    Double[] FLAG200OP = Flag200Function(FLAG100OP[5], y);  // E, Vc', Uc'
                    Double[] FLAG300OP = Flag300Function(weights, FLAG100OP, FLAG200OP);  // Va' Ua' Vb' Ub'
                    Double[] FLAG400OP = FLag400Function(FLAG100OP, FLAG200OP, FLAG300OP, x1, x2);  // w1' w2' ... w9'
                    Double[] FLAG500OP = newWs(n, weights, FLAG400OP);  // w1' w2' ... w9'
                    weights = FLAG500OP;
                }
                weightsPrinter(weights);
                formatPrinter(evaluationSetE(weights));
            }
        } else if (args.length == 12 && args[0].equals("800")) {
            Double[] weights = readWeights(args);

            // read x1 and x2
            Double n = Double.parseDouble(args[10]);
            ArrayList<Student> students = fileReader(100);
            int count = 0;
            Double prevE = Double.MAX_VALUE;
            int T = Integer.parseInt(args[11]);
            for (int i = 0; i < T; i++) {
                count ++;
                for (Student s : students) {
                    Double x1 = s.HW;
                    Double x2 = s.midterm;
                    Double y = s.A;

                    Double[] sinfo = new Double[]{x1, x2, y};

                    Double[] FLAG100OP = Flag100Function(weights, x1, x2);  // ua, va, ub, vb, uc, vc
                    Double[] FLAG200OP = Flag200Function(FLAG100OP[5], y);  // E, Vc', Uc'
                    Double[] FLAG300OP = Flag300Function(weights, FLAG100OP, FLAG200OP);  // Va' Ua' Vb' Ub'
                    Double[] FLAG400OP = FLag400Function(FLAG100OP, FLAG200OP, FLAG300OP, x1, x2);  // w1' w2' ... w9'
                    Double[] FLAG500OP = newWs(n, weights, FLAG400OP);  // w1' w2' ... w9'
                    weights = FLAG500OP;
                }
                if (evaluationSetE(weights) > prevE) {
                    break;
                }
                prevE = evaluationSetE(weights);
            }
            System.out.println(count);
            weightsPrinter(weights);
            formatPrinter(evaluationSetE(weights));
            formatPrinter(accuracy(weights));


        } else {
            System.out.println("Read the PDF for usage");
        }
    }

    static private Double accuracy (Double[] w) {
        ArrayList<Student> test = fileReader(300);
        int success = 0;
        for (Student s : test) {
            Double[] FLAG100OP = Flag100Function(w, s.HW, s.midterm);  // ua, va, ub, vb, uc, vc
            int result;

            if (FLAG100OP[5] >= 0.5) {
                result = 1;
            } else {
                result = 0;
            }

            if (result == s.A) {
                success ++;
            }
        }
        return 1.0*success/25;
    }

    static private Double evaluationSetE(Double[] w) {
        ArrayList<Student> test = fileReader(200);
        Double sum = 0.0;
        for (Student s : test) {
            Double[] FLAG100OP = Flag100Function(w, s.HW, s.midterm);  // ua, va, ub, vb, uc, vc
            sum += 0.5 * Math.pow((FLAG100OP[5] - s.A), 2);
        }

        return sum;
    }

    static private void studentsPrinter(ArrayList<Student> students) {
        for (Student s : students) {
            System.out.println(s.midterm+" "+s.HW+" "+s.A);
        }
    }

    static private void formatPrinter(Double w) {
            String out =String.format("%.5f", w);
            System.out.println(out);
    }

    static private void weightsPrinter(Double[] w) {
        for (int i = 1; i < w.length-1; i ++) {
            String out =String.format("%.5f", w[i]);
            System.out.print(out+" ");
        }
        String out =String.format("%.5f", w[w.length-1]);
        System.out.println(out);
    }

    static private Double[] newWs (Double n, Double[] oldw, Double[] pw) {
        Double[] res = new Double[oldw.length];
        for (int i = 1; i <oldw.length; i++) {
            res[i] = oldw[i] - n * pw[i-1];
        }
        return res;
    }

    /**
     * wij' = vi * uj'
     * @param F100 ua, va, ub, vb, uc, vc
     * @param F200 E, Vc', Uc'
     * @param F300 Va' Ua' Vb' Ub'
     * @return w1' w2' ... w9'
     */
    static private Double[] FLag400Function(Double[] F100, Double[] F200, Double[] F300, Double x1, Double x2) {
        Double[] OP = new Double[9];  //w1' w2' ... w9'
        OP[0] = 1.0 * F300[1];
        OP[1] = x1 * F300[1];
        OP[2] = x2 * F300[1];
        OP[3] = 1.0 * F300[3];
        OP[4] = x1 * F300[3];
        OP[5] = x2 * F300[3];
        OP[6] = 1.0 * F200[2];
        OP[7] = F100[1] * F200[2];
        OP[8] = F100[3] * F200[2];
        return OP;
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
        for (int i = 0; i < arr.length-1; i++) {
            if (arr[i] == 0) {
                arr[i] = 0.0;
            }
            String out =String.format("%.5f", arr[i]);
            System.out.print(out+" ");
        }
        String out =String.format("%.5f", arr[arr.length-1]);
        System.out.println(out);
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