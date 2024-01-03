package com.cyberello.ksfarm.data;

import java.util.ArrayList;
import java.util.Arrays;

public class PapayaMeta {

    public ArrayList<String> plotNames;
    public int[] plotRows;

    public static PapayaMeta getPapayaMeta() {

        PapayaMeta papayaMeta = new PapayaMeta();

        papayaMeta.plotNames = new ArrayList<>(Arrays.asList("Mercury", "Gemini", "Apollo"));
        papayaMeta.plotRows = new int[]{12, 0, 0};

        return papayaMeta;
    }
}
