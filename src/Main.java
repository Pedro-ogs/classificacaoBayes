import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static List<Integer> typesOfClasses = Arrays.asList(0, 1);

    public static void main(String[] args) {
        List<Data> baseTreino = new ArrayList<>();
        List<Data> baseTeste = new ArrayList<>();
        splitData(baseTreino, baseTeste);

        int numClass = typesOfClasses.size();
        int numInput = baseTreino.get(0).getInput().length;
        Classificador classificador = new Classificador(numClass, numInput);
        classificador.fillMatrix(baseTreino);

        classificador.setTrainingBase(baseTreino);
        int baseTreinoErro = 0;
        for (Data sample : baseTreino) {
            double classification = classificador.classify(sample);
            if (classification != sample.getOutput()[0] )
                baseTreinoErro += 1;
        }

        classificador.setTrainingBase(baseTreino);
        int baseTesteErro = 0;
        for (Data sample : baseTreino) {
            double classification = classificador.classify(sample);
            if (classification != sample.getOutput()[0] )
                baseTreinoErro += 1;
        }

        System.out.println("Erro de classificação base de treino: " + baseTreinoErro);
        System.out.println("Erro de classificação base de teste: " + baseTesteErro);

    }

    public static List<Data> readData(String fileName){
        List<Data> ecoliData = new LinkedList<>();

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fileReader);

            String line;

            while((line = br.readLine()) != null){
                String[] split = line.split("  ");
                double[] input = new double[split.length - 1];
                double[] output = null;

                for (int i = 0; i < input.length; i++){

                    input[i] = Double.parseDouble(split[i]);
                    if (i == input.length - 1) {
                        output = handleOutput(split[i + 1]);
                    }
                }
                if (!Arrays.stream(input).allMatch(x -> x == 0.0) && output != null) {
                    ecoliData.add(new Data(input, output));
                }
            }

            br.close();

        } catch (IOException e){
            System.err.println("Arquivo " + fileName + " não encontrado!");
        }

        return ecoliData;
    }

    private static double[] handleOutput(String value) {
        if (value.equals(" cp"))
            return new double[]{0, 1};
        else if (value.equals(" im"))
            return new double[]{1, 0};

        return null;
    }

    private static List<String> readDataBase(String fileName){
        List<String> dataBase = new LinkedList<>();

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fileReader);

            String line;
            while((line = br.readLine()) != null){
                dataBase.add(line);
            }
            br.close();

        }catch (IOException e) {
            System.err.println("Arquivo " + fileName + " não encontrado!");
        }

        return dataBase;
    }
    private static void createBasesFiles(){
        List<String> dataBase = readDataBase("ecoli.data");
        createFile("baseCP.txt" , "cp", dataBase);//Criar o txt para o resultado CP
        createFile("baseIM.txt" , "im", dataBase);//Criar o txt para o resultado IM
    }

    private static void splitData(List<Data> baseTreino, List<Data> baseTeste) {
        createBasesFiles();
        List<Data> baseCp = readData("baseCP.txt");
        List<Data> baseIm = readData("baseIM.txt");

        int size = (int) (baseCp.size() * 0.7);
        Collections.shuffle(baseCp);

        for (int i = 0; i < baseCp.size(); i++){
            if (i < size) {
                baseTreino.add(baseCp.get(i));
            }else {
                baseTeste.add(baseCp.get(i));
            }
        }

        size = (int) (baseIm.size() * 0.7);
        Collections.shuffle(baseIm);

        for (int i = 0; i < baseIm.size(); i++){
            if (i < size) {
                baseTreino.add(baseIm.get(i));
            }else {
                baseTeste.add(baseIm.get(i));
            }
        }

        Collections.shuffle(baseTreino);
        Collections.shuffle(baseTeste);

    }

    private static void createFile(String fileName, String baseClassification, List<String> dataBase) {
        try {
            FileWriter fw = new FileWriter(fileName);
            BufferedWriter bw = new BufferedWriter(fw);

            for (String value : dataBase) {
                if (isBaseType(value, baseClassification)) {
                    bw.write(value);
                    bw.newLine();
                }
            }

            bw.close();
        } catch (IOException e) {
            System.err.println("Erro ao salvar o arquivo: " + e.getMessage());
        }
    }

    private static boolean isBaseType(String value, String baseClassification) {
        String[] array = value.split(" ");

        return array[array.length-1].trim().equals(baseClassification);
    }

    public static List<Data> getBaseByClass(List<Data> base, int nameType) {
        return base.stream()
                .filter(x -> x.getOutput()[0] == nameType)
                .toList();
    }

    public static double[] getSamples(List<Data> base, int sampleNumber) {
        double[] samples = new double[base.size()];

        for (int i = 0; i < samples.length; i++) {
            samples[i] = base.get(i).getInput()[sampleNumber];
        }

        return samples;
    }

}
