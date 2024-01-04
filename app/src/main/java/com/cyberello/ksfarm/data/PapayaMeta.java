package com.cyberello.ksfarm.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class PapayaMeta {

    public ArrayList<String> plotNames;
    public ArrayList<ArrayList<String>> plotRows;

    public static PapayaMeta getPapayaMeta() {

        PapayaMeta papayaMeta = new PapayaMeta();

        papayaMeta.plotNames = new ArrayList<>(Arrays.asList("Mercury", "Gemini", "Apollo"));

        ArrayList<String> plotRowNames = new ArrayList<>(Arrays.asList("A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "A10", "A11", "A12"));

        papayaMeta.plotRows = new ArrayList<>(Collections.singletonList(plotRowNames));

        return papayaMeta;
    }
}
