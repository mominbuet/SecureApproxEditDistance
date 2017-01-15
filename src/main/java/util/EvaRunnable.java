package util;

import java.util.Arrays;

import org.apache.commons.cli.ParseException;

import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Mode;
import flexsc.Party;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class EvaRunnable<T> extends network.Client implements Runnable {

    public abstract void prepareInput(CompEnv<T> gen) throws Exception;

    public abstract void secureCompute(CompEnv<T> gen) throws Exception;

    public abstract int prepareOutput(CompEnv<T> gen) throws Exception;
    Mode m;
    int port;
    String host;
    protected String[] args;
    public boolean verbose = false;
    public ConfigParser config;
    private int res = -2;
    private List listeners = new ArrayList();
    Map<Integer, String> queryCopies;
    int queryIndex;

    public void setParameter(ConfigParser config, String[] args,String host, Map<Integer, String> queryCopies, int queryIndex) {
        this.m = Mode.getMode(config.getString("Mode"));
        this.port = config.getInt("Port");
//        host = config.getString("Host");
        this.host = host;
        this.args = args;
        this.config = config;
        this.queryIndex = queryIndex;
        this.queryCopies = queryCopies;
    }

    public void setParameter(Mode m, String host, int port) {
        this.m = m;
        this.port = port;
        this.host = host;
    }

    @Override
    public void run() {
        try {
            if (verbose) {
                System.out.println("connecting, I am EVA");
            }
            connect(host, port);
            if (verbose) {
                System.out.println("connected");
            }

            @SuppressWarnings("unchecked")
            CompEnv<T> env = CompEnv.getEnv(m, Party.Bob, this);

            double s = System.nanoTime();
            Flag.sw.startTotal();
            prepareInput(env);
            os.flush();
            secureCompute(env);
            os.flush();
            prepareOutput(env);
//            os.flush();
            Flag.sw.stopTotal();
            double e = System.nanoTime();

            if (verbose) {
                System.out.println("Eva running time:" + (e - s) / 1e9);
                System.out.println("Number Of AND Gates:" + env.numOfAnds);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            disconnect();
        }
        notifyListeners();
    }

    private void notifyListeners() {
        for (Iterator iter = listeners.iterator(); iter.hasNext();) {
            WorkerListenerEvaluator listener = (WorkerListenerEvaluator) iter.next();
            listener.workDoneEvaluator(this, queryCopies, queryIndex);
        }
    }

    public void registerWorkerListener(WorkerListenerEvaluator listener) {
        listeners.add(listener);
    }

    public int getResult() {
        return res;
    }
//	@SuppressWarnings("rawtypes")

//    public static void main(String[] args) throws InstantiationException, IllegalAccessException, ParseException, ClassNotFoundException {
//        ConfigParser config = new ConfigParser("Config.conf");
//        args = new String[2];
////        args[0] = "example.DecryptHE";
//        args[0] = "example.HammingDistanceString";
//        args[1] = "";
//        for (int i = 0; i < 20; i++) {
//            args[1] += "A";
//        }
//
////        args[2] = args[1].length()+"";
////        args[1] = "12";
////        args[3] = "100";
//        Class<?> clazz = Class.forName(args[0] + "$Evaluator");
//        EvaRunnable run = (EvaRunnable) clazz.newInstance();
//        run.setParameter(config, Arrays.copyOfRange(args, 1, args.length));
//        run.run();
//
//        if (Flag.CountTime) {
//            Flag.sw.print();
//        }
//        if (Flag.countIO) {
//            run.printStatistic();
//        }
//    }
}
