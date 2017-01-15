/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.List;
import java.util.Map;

/**
 *
 * @author azizmma
 */
public interface WorkerListenerEvaluator {

    public void workDoneEvaluator(EvaRunnable run, Map<Integer, String> queryShingles, int queryIndex);
}
