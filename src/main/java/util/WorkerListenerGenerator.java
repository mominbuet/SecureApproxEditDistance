/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Map;

/**
 *
 * @author azizmma
 */
public interface WorkerListenerGenerator {

    public void workDoneGenerator(GenRunnable run, Map<Integer, String> allkeys, Integer[] keys, int keyIndex);

}
