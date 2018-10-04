package com.samv;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

public class GameEvaluator {
    private List<String> battleGrid;
    private List<String> instructions;

    public GameEvaluator(String gridFile, String scriptFile) throws IOException{
        this.battleGrid = readFieldFile(gridFile);
        this.instructions = readScriptFile(scriptFile);
    }

    public List<String> readFieldFile(String fieldName){
        List<String> battleGrid = new ArrayList<String>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fieldName));
            String gridRow;
            while ((gridRow = reader.readLine()) != null){
                if(gridRow.length()>0) {
                    //Checking invalid characters in field file using regular expressions.
                    Pattern p = Pattern.compile("[^a-zA-Z.]+");
                    Matcher m = p.matcher(gridRow);
                    if(Pattern.matches("[A-Za-z.]+", gridRow.trim()))
                        battleGrid.add(gridRow.trim());
                    else {
                        System.err.println("Invalid characters in field file is found.");
                        System.err.println("Only accepted characters in field file is A to Z, a to z and .");
                        System.exit(0);
                    }
                }
            }
            reader.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }

        if(battleGrid.size() == 0){
            System.err.println("Field file to read mine locations is empty.Please, verify it.");
            System.exit(0);
        }


        return battleGrid;
    }

    public List<String> readScriptFile(String scriptName) {
        List<String> instructions = new ArrayList<String>();
        BufferedReader reader = null;

        try{
            reader= new BufferedReader(new FileReader(scriptName));
            String instruction;
            while((instruction = reader.readLine()) != null){
                if(instruction.length()>0)
                    instructions.add(instruction);
            }
            if(instructions.size() == 0) {
                System.err.print("Instructions file is empty. At least one instruction should be present");
                System.exit(0);
            }

        }catch (IOException ioe){
            ioe.printStackTrace();
        }finally {
            try {
                if (reader != null)
                    reader.close();
            }catch (IOException ioe1){
                ioe1.printStackTrace();
            }
        }
        return instructions;
    }

    public List<String> getBattleGrid() {
        return battleGrid;
    }


    public List<String> getInstructions() {
        return instructions;
    }

    public static void main(String[] args) {


	    if(args.length != 2){
	        System.out.println("Usage : java GameEvaluator FieldFile.txt ScriptFile.txt");
	        System.exit(0);
        }
        try {
            GameEvaluator gameEvaluator = new GameEvaluator(args[0], args[1]);
            StarFleetGrid starFleetGrid = new StarFleetGrid(gameEvaluator.getBattleGrid());
            starFleetGrid.runScript(gameEvaluator.getInstructions());
        }catch (IOException ioe){
	        ioe.printStackTrace();
        }

    }
}
