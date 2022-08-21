import java.io.*;
import java.util.HashMap;


public class Assembler {

    HashMap<String, Integer> symbolMap;
    int cp;

    public Assembler() {
        this.symbolMap = new HashMap<>();
        this.cp = 0;
        for (int i = 0; i <16 ; i++) {
            this.symbolMap.put("R"+i, i);
        }
        this.symbolMap.put("SCREEN", 16384);
        this.symbolMap.put("KBD", 24576);
    }

    public static void main(String[] args) {
        transFile("Add");
        transFile("Max");
        transFile("MaxL");
        transFile("Rect");
        transFile("RectL");
    }

    private static void transFile(String fileName){
        String input_path = "src/input_files/";
        String output_path = "src/output_files/";
        BufferedReader br = null;
        Assembler assembler = new Assembler();
        try {
            FileWriter myWriter = new FileWriter(output_path + fileName + ".hack");
            br = new BufferedReader(new FileReader(input_path + fileName + ".txt"));
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                String valLine = assembler.extractValue(line);
                if (valLine.length() >0)  myWriter.write(valLine+"\n");
            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private String extractValue(String str){
        str = str.split("//")[0];
        str = str.replaceAll(" ","");
        if (str.length() <1 ) return str;
        if (str.charAt(0) == '('){
            if (str.charAt(str.length()-1) != ')') return null;
            String inStr = str.substring(1,str.length()-1);
            this.symbolMap.put(inStr, this.cp);
            return "";
        }
        this.cp++;
        if (str.charAt(0) == '@'){
            str = str.substring(1);
            int addressString = isNumeric(str) ? Integer.parseInt(str) : extractSymbolName(str) ;
            return intToBinary16Bit(addressString);
        }
        String[] splitStr =  str.split("=");
        String dest = str.contains("=")? extrctDestenition(splitStr[0]): "000";
        String valArea = splitStr[splitStr.length-1];
        String[] splitVal = valArea.split(";");
        String comp = splitVal[0];
        char bitA = comp.contains("M") ? '1': '0';
        comp = extractComp(comp);
        String jump = valArea.contains(";")? extractJump(splitVal[1]) : "000";
        String ans = "111" + bitA + comp + dest + jump;
        return ans;
    }

    private int extractSymbolName(String key) {
        if (!this.symbolMap.containsKey(key)) this.symbolMap.put(key, this.symbolMap.size()-2);
        return this.symbolMap.get(key);
    }

    private  String extractComp(String comp) {
        comp = comp.replaceAll("M","A");
        switch(comp) {
            case "0":
                return "101010";
            case "1":
                return "111111";
            case "-1":
                return "111010";
            case "D":
                return "001100";
            case "A":
                return "110000";
            case "!D":
                return "001101";
            case "!A":
                return "110001";
            case "-D":
                return "001111";
            case "-A":
                return "110011";
            case "D+1":
                return "011111";
            case "A+1":
                return "110111";
            case "D-1":
                return "001110";
            case "A-1":
                return "110010";
            case "D+A":
                return "000010";
            case "D-A":
                return "010011";
            case "A-D":
                return "000111";
            case "D&A":
                return "000000";
            case "D|A":
                return "010101";
            default:
                if (comp.contains("+") || comp.contains("|") || comp.contains("&")){
                    return extractComp(reverse(comp));
                }
        }
        return "";
    }

    private String extractJump(String str) {
        switch(str) {
            case "JGT":
                return "001";
            case "JEQ":
                return "010";
            case "JGE":
                return "011";
            case "JLT":
                return "100";
            case "JNE":
                return "101";
            case "JLE":
                return "110";
            case "JMP":
                return "111";
        }
        return "";
    }

    private  String extrctDestenition(String dest) {
        char[] ans = {'0', '0', '0'};
        if (dest.contains("M")) ans[2] = '1';
        if (dest.contains("D")) ans[1] = '1';
        if (dest.contains("A")) ans[0] = '1';
        return String.valueOf(ans);
    }

    private String intToBinary16Bit(int num) {
        String ans ="";
        for (int i = 15; i >=0 ; i--) {
            int pow = (int) Math.pow(2,i);
            if (num >= pow){
                num = num-pow;
                ans+= '1';
            }else{
                ans+="0";
            }
        }
        return ans;
    }


    private int binaryToInt(String str) {
        System.out.println(str);
        str = reverse(str);
        int sum = 0;
        for (int i = 0; i < str.length(); i++) {
            sum += Math.pow(2,i)*str.charAt(i);
        }
        return sum;
    }

    private String reverse(String str) {
        if (str == null) return null;
        StringBuilder strReverse = new StringBuilder(str).reverse();
        return strReverse.toString();
    }


    public boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
