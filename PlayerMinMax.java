/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Equipe08;


import java.util.List;

import jogo.AbstractPlayer;
import jogo.Casa;
import jogo.Jogada;
import jogo.JogoOthello;
import jogo.JogoVelha;
/**
 *
 * @author Lucas
 */
public class PlayerMinMax extends AbstractPlayer{

    public PlayerMinMax(int depth) {
        super(depth);
    }
    private final int DEPTH = 5; //Profundidade do MINMAX
    private long nos = 0; //TESTE (numero de nós visitados)
    private final int MAX = 1;
    private final int MIN = -1;
    private AbstractPlayer playerOponente;
    int valorPosicao1[][]={         { 99, -8,  8,  6,  6,  8, -8, 99 },
                                    { -8,-24, -4, -3, -3, -4,-24, -8 },
                                    {  8, -4,  7,  4,  4,  7, -4,  8 },
                                    {  6, -3,  4,  0,  0,  4, -3,  6 },
                                    {  6, -3,  4,  0,  0,  4, -3,  6 },
                                    {  8, -4,  7,  4,  4,  7, -4,  8 },
                                    { -8,-24, -4, -3, -3, -4,-24, -8 },
                                    { 99, -8,  8,  6,  6,  8, -8, 99 } };   
    private boolean flagvalorPosicao2;
    
    private int valorPosicao2[][]={ { 99, -8,  8,  6,  6,  8, -8, 99 },  
                                    { -8,-24,  0,  1,  1,  0,-24, -8 },
                                    {  8,  0,  7,  4,  4,  7,  0,  8 },
                                    {  6,  1,  4,  1,  1,  4,  1,  6 },
                                    {  6,  1,  4,  1,  1,  4,  1,  6 },
                                    {  8,  0,  7,  4,  4,  7,  0,  8 },
                                    { -8,-24,  0,  1,  1,  0,-24, -8 },
                                    { 99, -8,  8,  6,  6,  8, -8, 99 } } ;
    
    public float evaluateState(int[][] tab, Casa casa){
        float soma_heuristicas=0;        
        soma_heuristicas+= heuristicaPosicional(tab) * 8 ;         //heuristica1
        soma_heuristicas+= heuristicaMobilidade(tab) * 2 ;         //heuristica2
        soma_heuristicas+= heuristicaPecasVantagem(tab) * 1 ;      //heuristica3
        //soma_heuristicas+= heuristicaEstabilidade(tab) * 0 ;       //heuristica4
        soma_heuristicas+= heuristicaWinLose(tab) * 10 ;            //heuristica5
        return soma_heuristicas;
    }
    
    @Override
    public Casa jogar(int[][] tab) {
        JogoOthello jogo = new JogoOthello();
        List<Jogada> jogadas = jogo.getJogadasValidas(tab, getMinhaMarcaTabuleiro());
        //Verifica os especialistas
        Casa casaEspecialista = jogadasEapecialistas(tab,jogadas);
        if (casaEspecialista!=null){ //Se encontrou um caso verificado
            return casaEspecialista; //Retorna o caso
        }
        //Inicia a busca da melhor jogada através das heuristicas
        float[] valor_jogada = new float[jogadas.size()];
        int[][] temp;
        int maior_indice = 0;
        float maior = Integer.MIN_VALUE;
        criaOponente(); // cria oponente para poder testar jogadas
        for(int i=0;i<jogadas.size();i++){
            temp = criarTemp(tab);
            jogo.efetuar_jogada(temp, jogadas.get(i).getCasa(), this); //efetua a jogada
            valor_jogada[i]=minmax(DEPTH, temp, MIN, maior, jogadas.get(i).getCasa()); //Chama o metodo minmax
            if(valor_jogada[i]>maior){
                maior=valor_jogada[i];
                maior_indice=i;
            }                  
        }
        System.out.println("Nós visitados: "+nos);
        try {
            return jogadas.get(maior_indice).getCasa();
        } catch (Exception e) { // caso tenha erro na jogada ele tenta retornar a primeira
            try {
                return jogadas.get(0).getCasa();
            } catch (Exception e2) {// caso não exista jogada o player passa a vez
                return new Casa(-1, -1);
            }
        }
    }
    
    public Casa jogadasEapecialistas(int[][] tab, List<Jogada> jogadas){
//        for (Jogada jogada : jogadas) { //Prioridade 1
//            if(especialistaCantos(tab,jogada.getCasa())){ //Especialista 1
//                return jogada.getCasa();
//            }
//        }
        for (Jogada jogada : jogadas) { //Prioridade 2
            if(especialistaTaticaTomarCantos(tab,jogada.getCasa())){ //Especialista 2
                return jogada.getCasa();
            }
        }
        for (Jogada jogada : jogadas) { //Prioridade 3
            if(especialistaPreparaTomarCantos(tab,jogada.getCasa())){ //Especialista 3
                return jogada.getCasa();
            }
        }
        return null;
    }
    
    
        
    //------------------------------------MIN-MAX----------------------------
    public float minmax(int depth, int[][] tab, int min_ou_max, float referencia, Casa casa){
        nos++;
        JogoOthello jogo = new JogoOthello();
        if((depth==1)||(jogo.teste_terminal(tab, getMarcaTabuleiro())!=0)){  //nó folha
            return evaluateState(tab,casa);//calcula as heuristicas
        }
        if(min_ou_max==MIN){
            List<Jogada> jogadas = jogo.getJogadasValidas(tab, getMarcaTabuleiroOponente()); //Pega as jogadas do oponente
            float valor_jogada; //Cria um vetor para cada jogada receber o valor dos nós filhos
            int[][] temp; //Cria um tabuleiro temporario para simular jogadas
            float menor = Integer.MAX_VALUE;
            for(int i=0;i<jogadas.size();i++){    //Percorre todas as jogadas possiveis
                temp = criarTemp(tab); //Iguala o temporario ao original
                jogo.efetuar_jogada(temp, jogadas.get(i).getCasa(), playerOponente);  //efetua a jogada oponente
                valor_jogada=minmax(depth-1, temp, MAX, menor,jogadas.get(i).getCasa());//calcula as heuristicas  
                if (valor_jogada<menor){
                    menor=valor_jogada;
                    if(menor<referencia){
                        return menor;
                    }   
                }
            }       
            if(jogadas.size()>0){
                return menor;
            }else{
                return minmax(depth-1, tab, MAX, menor, null); //Não há jogadas possiveis (passa a vez)
            }
        }
        else{
            List<Jogada> jogadas = jogo.getJogadasValidas(tab, getMinhaMarcaTabuleiro()); //Pega as jogadas possiveis
            float valor_jogada; //Cria um vetor para cada jogada receber o valor dos nós filhos
            int[][] temp; //Cria um tabuleiro temporario para simular jogadas
            float maior = Integer.MIN_VALUE;
            for(int i=0;i<jogadas.size();i++){   //Percorre todas as jogadas possiveis
                temp = criarTemp(tab);  //Iguala o temporario ao original
                jogo.efetuar_jogada(temp, jogadas.get(i).getCasa(), this); //efetua a jogada
                valor_jogada = minmax(depth-1, temp, MIN, maior, jogadas.get(i).getCasa()); //calcula as heuristicas 
                if(valor_jogada>maior){
                    maior=valor_jogada;
                    if(maior>referencia){
                        return maior;
                    }
                }
            }
            if(jogadas.size()>0){
                return maior;
            }else{
                return minmax(depth-1, tab, MIN, maior, null); //Não há jogadas possiveis (passa a vez)
            }
        }
    }
    //------------------------------HEURISTICAS---------------------------------
    public int heuristicaPosicional(int[][] tab){ //Heuristica 1
        adjacenteCantos(tab);
        int acumulado = 0;
        int i, j;
        if(flagvalorPosicao2){
            acessoBordas(tab);
            for(i=0;i<8;i++){
                for(j=0;j<8;j++){
                    if(tab[i][j]==getMinhaMarcaTabuleiro()){
                        acumulado += valorPosicao2[i][j];
                    }
                    else if(tab[i][j]==getMarcaTabuleiroOponente()){
                        acumulado -= valorPosicao2[i][j];
                    }
                }
            }
        }else{
            for(i=0;i<8;i++){
                for(j=0;j<8;j++){
                    if(tab[i][j]==getMinhaMarcaTabuleiro()){
                        acumulado += valorPosicao1[i][j];
                    }
                    else if(tab[i][j]==getMarcaTabuleiroOponente()){
                        acumulado -= valorPosicao1[i][j];
                    }
                }
            }
        }
        return acumulado;
    }
    
    public int heuristicaMobilidade(int[][] tab){ //Heuristica 2
        JogoOthello jogo = new JogoOthello();
        //Num jogadas de J1 - Num jogadas de J2
        List<Jogada> jogadasPlayer = jogo.getJogadasValidas(tab, getMinhaMarcaTabuleiro());
        List<Jogada> jogadasOponente = jogo.getJogadasValidas(tab, getMarcaTabuleiroOponente());
        return jogadasPlayer.size()-jogadasOponente.size();
    }
    
    public int heuristicaPecasVantagem(int[][] tab){ //Heuristica 3
        int acumulado=0;
        int i, j;
        for(i=0;i<8;i++){
            for(j=0;j<8;j++){
                if(this.getMinhaMarcaTabuleiro()==tab[i][j]){
                    acumulado++;
                }
                else if(this.getMarcaTabuleiroOponente()==tab[i][j]){
                    acumulado--;
                }
            }
        }
        return acumulado;
    }
    
    public int heuristicaEstabilidade(int[][] tab){ //Heuristica 4
        if((tab[0][0]==0) && (tab[7][0]==0) && (tab[0][7]==0) && (tab[7][7]==0)){
            return 0;
        }
        int x,y;
        int acumulado=0;
        for(x=0;x<8;x++){
            for(y=0;y<8;y++){
                if(tab[x][y]==this.getMinhaMarcaTabuleiro()){
                    if(estabilidadeSE(tab,x,y)||estabilidadeSD(tab,x,y)||estabilidadeIE(tab,x,y)||estabilidadeID(tab,x,y)){
                        acumulado+=1;
                    }
                }
                else if(tab[x][y]==this.getMarcaTabuleiroOponente()){
                    if(estabilidadeSE(tab,x,y)||estabilidadeSD(tab,x,y)||estabilidadeIE(tab,x,y)||estabilidadeID(tab,x,y)){
                        acumulado-=1;
                    }
                }
            }
        }
        return acumulado;
    }
    
    private int heuristicaWinLose(int[][] tab){ //Heuristica 5
        JogoOthello jogo = new JogoOthello();
        if (jogo.teste_terminal(tab, getMarcaTabuleiro())!=0){
            int vantagem = heuristicaPecasVantagem(tab);
            if(vantagem > 0){
                return 10000;
            }
            if(vantagem < 0){
                return -10000;
            }
        }
        return 0;
    }
    
    
    //----------------------METODOS ESPECIALISTAS-------------------------------
    
    
    public boolean especialistaPreparaTomarCantos(int[][] tab, Casa casa){
        if (casa==null){
            return false;
        }
        if((casa.getLinha()==0)&&(casa.getColuna()==4)){
            if( (tab[0][0]==0)&&
                (tab[0][1]==getMarcaTabuleiroOponente())&&
                (tab[0][2]==0)&&
                (tab[0][3]==0)&&
                (tab[0][5]==0)) {
                return true;
            }
        }
        if((casa.getLinha()==7)&&(casa.getColuna()==4)){
            if( (tab[7][0]==0)&&
                (tab[7][1]==getMarcaTabuleiroOponente())&&
                (tab[7][2]==0)&&
                (tab[7][3]==0)&&
                (tab[7][5]==0)) {
                return true;
            }
        }
        if((casa.getLinha()==0)&&(casa.getColuna()==3)){
            if( (tab[0][7]==0)&&
                (tab[0][6]==getMarcaTabuleiroOponente())&&
                (tab[0][5]==0)&&
                (tab[0][4]==0)&&
                (tab[0][2]==0)) {
                return true;
            }
        }
        if((casa.getLinha()==7)&&(casa.getColuna()==3)){
            if( (tab[7][7]==0)&&
                (tab[7][6]==getMarcaTabuleiroOponente())&&
                (tab[7][5]==0)&&
                (tab[7][4]==0)&&
                (tab[7][2]==0)) {
                return true;
            }
        }
        if((casa.getLinha()==4)&&(casa.getColuna()==0)){
            if( (tab[0][0]==0)&&
                (tab[1][0]==getMarcaTabuleiroOponente())&&
                (tab[2][0]==0)&&
                (tab[3][0]==0)&&
                (tab[5][0]==0)) {
                return true;
            }
        }
        if((casa.getLinha()==4)&&(casa.getColuna()==7)){
            if( (tab[0][7]==0)&&
                (tab[1][7]==getMarcaTabuleiroOponente())&&
                (tab[2][7]==0)&&
                (tab[3][7]==0)&&
                (tab[5][7]==0)) {
                return true;
            }
        }
        if((casa.getLinha()==3)&&(casa.getColuna()==0)){
            if( (tab[7][0]==0)&&
                (tab[6][0]==getMarcaTabuleiroOponente())&&
                (tab[5][0]==0)&&
                (tab[4][0]==0)&&
                (tab[2][0]==0)) {
                return true;
            }
        }
        if((casa.getLinha()==3)&&(casa.getColuna()==7)){
            if( (tab[7][7]==0)&&
                (tab[6][7]==getMarcaTabuleiroOponente())&&
                (tab[5][7]==0)&&
                (tab[4][7]==0)&&
                (tab[2][7]==0)) {
                return true;
            }
        }
        return false;
    }
    
    
    public boolean especialistaTaticaTomarCantos(int[][] tab, Casa casa){
        if (casa==null){
            return false;
        }
        if((casa.getLinha()==0)&&(casa.getColuna()==2)){
            if( (tab[0][0]==0)&&
                (tab[0][1]==getMarcaTabuleiroOponente())&&
                (tab[0][3]==0)&&
                (tab[0][4]==getMinhaMarcaTabuleiro())&&
                (tab[0][5]==0)&&
                (tab[1][1]==0)) {
                return true;
            }
        }
        if((casa.getLinha()==7)&&(casa.getColuna()==2)){
            if( (tab[7][0]==0)&&
                (tab[7][1]==getMarcaTabuleiroOponente())&&
                (tab[7][3]==0)&&
                (tab[7][4]==getMinhaMarcaTabuleiro())&&
                (tab[7][5]==0)&&
                (tab[6][1]==0)) {
                return true;
            }
        }
        if((casa.getLinha()==0)&&(casa.getColuna()==5)){
            if( (tab[0][7]==0)&&
                (tab[0][6]==getMarcaTabuleiroOponente())&&
                (tab[0][4]==0)&&
                (tab[0][3]==getMinhaMarcaTabuleiro())&&
                (tab[0][2]==0)&&
                (tab[1][6]==0)) {
                return true;
            }
        }
        if((casa.getLinha()==7)&&(casa.getColuna()==5)){
            if( (tab[7][7]==0)&&
                (tab[7][6]==getMarcaTabuleiroOponente())&&
                (tab[7][4]==0)&&
                (tab[7][3]==getMinhaMarcaTabuleiro())&&
                (tab[7][2]==0)&&
                (tab[6][6]==0)) {
                return true;
            }
        }
        if((casa.getLinha()==2)&&(casa.getColuna()==0)){
            if( (tab[0][0]==0)&&
                (tab[1][0]==getMarcaTabuleiroOponente())&&
                (tab[3][0]==0)&&
                (tab[4][0]==getMinhaMarcaTabuleiro())&&
                (tab[5][0]==0)&&
                (tab[1][1]==0)) {
                return true;
            }
        }
        if((casa.getLinha()==2)&&(casa.getColuna()==7)){
            if( (tab[0][7]==0)&&
                (tab[1][7]==getMarcaTabuleiroOponente())&&
                (tab[3][7]==0)&&
                (tab[4][7]==getMinhaMarcaTabuleiro())&&
                (tab[5][7]==0)&&
                (tab[1][6]==0)) {
                return true;
            }
        }
        if((casa.getLinha()==5)&&(casa.getColuna()==0)){
            if( (tab[7][0]==0)&&
                (tab[6][0]==getMarcaTabuleiroOponente())&&
                (tab[4][0]==0)&&
                (tab[3][0]==getMinhaMarcaTabuleiro())&&
                (tab[2][0]==0)&&
                (tab[6][1]==0)) {
                return true;
            }
        }
        if((casa.getLinha()==5)&&(casa.getColuna()==7)){
            if( (tab[7][7]==0)&&
                (tab[6][7]==getMarcaTabuleiroOponente())&&
                (tab[4][7]==0)&&
                (tab[3][7]==getMinhaMarcaTabuleiro())&&
                (tab[2][7]==0)&&
                (tab[6][6]==0)) {
                return true;
            }
        }
        return false;
    }
    
        public boolean especialistaCantos(int[][] tab, Casa casa){
        if(
            (casa.getLinha()==0)&&(casa.getColuna()==0) ||
            (casa.getLinha()==0)&&(casa.getColuna()==7) ||
            (casa.getLinha()==7)&&(casa.getColuna()==0) ||
            (casa.getLinha()==7)&&(casa.getColuna()==7) 
          )
        {
            return true;
        }else{
            return false;
        }
    }
    
    
    //------------------------METODOS AUXILIARES--------------------------------
    
    public int[][] criarTemp (int[][] tab){ //Retorne uma copia do tabuleiro
        int i, j;
        int[][] temp = new int[8][8];
        for(i=0;i<8;i++){
            for(j=0;j<8;j++){
                temp[i][j]=tab[i][j];
            }
        }
        return temp;
    }
    
    public void criaOponente(){
        playerOponente = new PlayerGreedy(-1);
        playerOponente.setMarcaTabuleiro(this.getMarcaTabuleiroOponente());
        playerOponente.setMarcaTabuleiroOponente(this.getMinhaMarcaTabuleiro());
    }
    
    //Estabilidade Superior Esquerdo
    public boolean estabilidadeSE(int[][] tab, int y, int x){
        //OBS: X está como COLUNA e Y está como LINHA
        //Diagonal superior
        int marca = tab[y][x];
        int i,j;
        for(i=x; i>=0; i--){
            for(j=y; j>=0; j--){
                if(tab[j][i]!=marca){
                    return false;
                }
            }
        }
        boolean diagonal1 = true, diagonal2 = true;
        int aux=1;
        //Diagonal superior
        for(i=x+aux; ((y-aux>=0) || (x+aux<=7)) && i<8; aux++, i++){
            for(j=y-aux; j>=0; j--){
                if(tab[j][i]!=marca){
                    diagonal1 = false;
                }
            }
        }
        //Diagonal inferior
        aux=1;
        for(j=y+aux; ((x-aux>=0) || (y+aux<=7)) && j<8; aux++, j++){
            for(i=x-aux; i>=0; i--){
                if(tab[j][i]!=marca){
                    diagonal2 = false;
                }
            }
        }
        return (diagonal1 || diagonal2);
    }
    
    //Estabilidade Superior Direito
    public boolean estabilidadeSD(int[][] tab, int y, int x){
        //OBS: X está como COLUNA e Y está como LINHA
        //Diagonal superior
        int marca = tab[y][x];
        int i,j;
        for(i=x; i<8; i++){
            for(j=y; j>=0; j--){
                if(tab[j][i]!=marca){
                    return false;
                }
            }
        }
        boolean diagonal1 = true, diagonal2 = true;
        int aux=1;
        //Diagonal inferior
        for(j=y+aux; ((y+aux<=7) || (x+aux<=7)) && j<8; aux++, j++){
            for(i=x+aux; i<=7; i++){
                if(tab[j][i]!=marca){
                    diagonal1 = false;
                }
            }
        }
        //Diagonal superior
        aux=1;
        for(i=x-aux; ((x-aux>=0) || (y-aux>=0)) && i>=0; aux++, i--){
            for(j=y-aux; j>=0; j--){
                if(tab[j][i]!=marca){
                    diagonal2 = false;
                }
            }
        }
        return (diagonal1 || diagonal2);
    }
    
    //Estabilidade Inferior Esquerdo
    public boolean estabilidadeIE(int[][] tab, int y, int x){
        //OBS: X está como COLUNA e Y está como LINHA
        //Diagonal superior
        int marca = tab[y][x];
        int i,j;
        for(i=x; i>=0; i--){
            for(j=y; j<8; j++){
                if(tab[j][i]!=marca){
                    return false;
                }
            }
            
        }
        boolean diagonal1 = true, diagonal2 = true;
        int aux=1;
        for(j=y-aux; ((y-aux>=0) || (x-aux>=0))&& j>=0; aux++, j--){
            for(i=x-aux; i>=0; i--){
                if(tab[j][i]!=marca){
                    diagonal1 = false;
                }
            }
            
        }
        //Diagonal inferior
        aux=1;
        for(i=x+aux; ((x+aux<=7) || (y+aux<=7)) && i<8; aux++, i++){
            for(j=y+aux; j<=7; j++){
                if(tab[j][i]!=marca){
                    diagonal2 = false;
                }
            }
            
        }
        return (diagonal1 || diagonal2);
    }
    
        //Estabilidade Inferior Esquerdo
    public boolean estabilidadeID(int[][] tab, int y, int x){
        int marca = tab[y][x];
        int i,j;
        for(i=x; i<8; i++){
            for(j=y; j<8; j++){
                if(tab[j][i]!=marca){
                    return false;
                }
            }
        }
        boolean diagonal1 = true, diagonal2 = true;
        //Diagonal superior
        int aux=1;
        for(j=y-aux; ((y-aux>=0) || (x+aux<=7))&& j>=0; aux++, j--){
            for(i=x+aux; i<=7; i++){
                if(tab[j][i]!=marca){
                    diagonal1 = false;
                }
            }
        }
        //Diagonal inferior
        aux=1;
        for(i=x-aux; ((x-aux>=0) || (y+aux<=7))&& i>=0; aux++, i--){
            for(j=y+aux; j<=7; j++){
                if(tab[j][i]!=marca){
                    diagonal2 = false;
                }
            }
        }
        return (diagonal1 || diagonal2);
    }
    
    public void adjacenteCantos(int[][] tab){ //Metodo usado na heuristicaPosicional
        flagvalorPosicao2 = false;
        if (tab[0][0] == 0){
            valorPosicao2[0][1] = -8;
            valorPosicao2[1][0] = -8;
            valorPosicao2[1][1] =-24;
        }else{
            valorPosicao2[0][1] = 12;
            valorPosicao2[1][0] = 12;
            valorPosicao2[1][1] =  8;
            flagvalorPosicao2 = true;
        }
        if (tab[0][7] == 0){
            valorPosicao2[0][6] = -8;
            valorPosicao2[1][7] = -8;
            valorPosicao2[1][6] =-24;
        }else{
            valorPosicao2[0][6] = 12;
            valorPosicao2[1][7] = 12;
            valorPosicao2[1][6] =  8;
            flagvalorPosicao2 = true;
        }
        if (tab[7][0] == 0){
            valorPosicao2[7][1] = -8;
            valorPosicao2[6][0] = -8;
            valorPosicao2[6][1] =-24;
        }else{
            valorPosicao2[7][1] = 12;
            valorPosicao2[6][0] = 12;
            valorPosicao2[6][1] =  8;
            flagvalorPosicao2 = true;
        }
        if (tab[7][7] == 0){
            valorPosicao2[7][6] = -8;
            valorPosicao2[6][7] = -8;
            valorPosicao2[6][6] =-24;
        }else{
            valorPosicao2[7][6] = 12;
            valorPosicao2[6][7] = 12;
            valorPosicao2[6][6] =  8;
            flagvalorPosicao2 = true;
        }
    }
    
    public void acessoBordas(int[][] tab){
        if ((tab[0][0] == getMinhaMarcaTabuleiro())||(tab[0][7] == getMinhaMarcaTabuleiro())){
            valorPosicao2[1][2] = 0;
            valorPosicao2[1][3] = 1;
            valorPosicao2[1][4] = 1;
            valorPosicao2[1][5] = 0;
            flagvalorPosicao2 = true;
        }else{
            valorPosicao2[1][2] = -4;
            valorPosicao2[1][3] = -3;
            valorPosicao2[1][4] = -3;
            valorPosicao2[1][5] = -4;
        }
        if ((tab[7][0] == getMinhaMarcaTabuleiro())||(tab[7][7] == getMinhaMarcaTabuleiro())){
            valorPosicao2[6][2] = 0;
            valorPosicao2[6][3] = 1;
            valorPosicao2[6][4] = 1;
            valorPosicao2[6][5] = 0;
            flagvalorPosicao2 = true;
        }else{
            valorPosicao2[6][2] = -4;
            valorPosicao2[6][3] = -3;
            valorPosicao2[6][4] = -3;
            valorPosicao2[6][5] = -4;
        }
        if ((tab[0][0] == getMinhaMarcaTabuleiro())||(tab[7][0] == getMinhaMarcaTabuleiro())){
            valorPosicao2[2][1] = 0;
            valorPosicao2[3][1] = 1;
            valorPosicao2[4][1] = 1;
            valorPosicao2[5][1] = 0;
            flagvalorPosicao2 = true;
        }else{
            valorPosicao2[2][1] = -4;
            valorPosicao2[3][1] = -3;
            valorPosicao2[4][1] = -3;
            valorPosicao2[5][1] = -4;
        }
        if ((tab[0][7] == getMinhaMarcaTabuleiro())||(tab[7][7] == getMinhaMarcaTabuleiro())){
            valorPosicao2[2][6] = 0;
            valorPosicao2[3][6] = 1;
            valorPosicao2[4][6] = 1;
            valorPosicao2[5][6] = 0;
            flagvalorPosicao2 = true;
        }else{
            valorPosicao2[2][6] = -4;
            valorPosicao2[3][6] = -3;
            valorPosicao2[4][6] = -3;
            valorPosicao2[5][6] = -4;
        }
    }
   
}
