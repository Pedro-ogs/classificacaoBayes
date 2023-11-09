import java.util.List;

public class Classificador {
    private double[][] media;
    private double[][] desvioPadrao;
    private List<Data> trainingBase;

    public Classificador(int numClasse, int numEntradas) {
        this.media = new double[numClasse][numEntradas];
        this.desvioPadrao = new double[numClasse][numEntradas];
    }

    public void setTrainingBase(List<Data> trainingBase) {
        this.trainingBase = trainingBase;
    }

    public double[][] getMedia() {
        return media;
    }

    public double[][] getDesvioPadrao() {
        return desvioPadrao;
    }

    public int classify(Data base){
        double firstClass = calculateProbability(base.getInput(), Main.typesOfClasses.get(0));
        double secondClass = calculateProbability(base.getInput(), Main.typesOfClasses.get(1));

        if(firstClass > secondClass){
            return 0;
        }else{
            return 1;
        }

    }

    private double gaussProbabilityDensity(double x, double value, double sd){
        sd = sd == 0.0 ? 0.00000001 : sd;
        double r = 1 / Math.sqrt(2 * Math.PI * sd);//r da infinito -- desvio padrão zero
        r = r * Math.exp(-(Math.pow(x - value, 2) / (2 * Math.pow(sd, 2))));
        return r;
    }

    private double probabilityDensity(double[] base, int nameType){
        double result = 1;

        for (int i = 0; i < base.length; i++){
            double mean = this.media[nameType][i];
            double sd = this.desvioPadrao[nameType][i];
            result *= gaussProbabilityDensity(base[i], mean, sd);
        }

        return result;
    }

    private double calculateProbability(double[] base, int nameType){
        double result = probabilityDensity(base, nameType) * getP(nameType);
        String numberFormated = String.format("%.8f", result).replace(",", ".");
        return Double.parseDouble(numberFormated);
    }

    private double getP(int nameType){
        double baseSize = this.trainingBase.size();
        double filteredBaseSize = Main.getBaseByClass(this.trainingBase, nameType).size();

        return filteredBaseSize / baseSize;
    }

    public void fillMatrix(List<Data> base){
        List<Integer> types = Main.typesOfClasses;

        for (Integer type : types) {
            List<Data> filteredBase = Main.getBaseByClass(base, type);
            int inputLength = filteredBase.get(0).getInput().length;
            for (int i = 0; i < inputLength; i++) {
                double[] samples = Main.getSamples(filteredBase, i);
                media(samples, type, i);
                desvioPadrao(samples, type, i);
            }
        }
    }

    public void media(double[] x, int nameType, int sampleNumber) {
        double soma = 0;

        for (int i = 0; i < x.length; i++)
            soma += x[i];

        double mean = soma / x.length;
        this.media[nameType][sampleNumber] = mean;
    }

    public void desvioPadrao(double[] x, int classType, int sampleNumber) {
        double mean = this.media[classType][sampleNumber];

        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            double result = Math.pow((x[i] - mean), 2);
            String numberFormated = String.format("%.8f", result).replace(",", ".");
            //return Double.parseDouble(numberFormated);
            sum += Double.parseDouble(numberFormated);
        }

        double r = (sum / x.length);
        this.desvioPadrao[classType][sampleNumber] = Math.sqrt(r);
    }
}
