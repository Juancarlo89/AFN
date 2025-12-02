package Models;

import Controllers.DatosAFN;


public class ExpReg {
   private AnalizadorLexico L;
   public AFN resultado;
   String expresion;
   private  DatosAFN datos;
   
   //Tokens temprales de los operadores
   private static final int Union = 10;  // |       
   private static final int Concatenacion = 20; // &   
   private static final int CPos = 30;   // +
   private static final int CK = 40;    // *
   private static final int CO = 50;    // ?
   private static final int PI = 60;    // (
   private static final int PD = 70;    // )
   private static final int CI = 80;    // [
   private static final int CD = 90;   // ]        
   private static final int Simb = 100;
   private static final int Guion = 110; // -
   private static final int Gi = 120;  // \        
   
   
   public ExpReg(String sigma, String AFD, DatosAFN datosEXT){
       //sigma = expresion; 
       this.expresion = sigma;
       L = new AnalizadorLexico(expresion, AFD); 
       this.datos = datosEXT;
   }
   
   public ExpReg(String AFD){
       L = new AnalizadorLexico(expresion, AFD);
   }
   
   public void SetExpression(String sigma){
       //sigma = expresion;
       this.expresion = sigma;
       L.SetSigma(sigma);
   }
   
   public boolean Conversion(){
       int token;
       AFN afn = new AFN();
       
       if(E(afn)){
           token = L.yylex();
           if(token == SimbEsp.FIN);
           this.resultado = afn; // guarda el resultado en la varibale 
           System.out.println(datos.getListaAFN());
           return true;
       }
       System.out.println("Error al convertir.");
       return false;
       
   }
   
   //E-> T Ep  (expresion completa y manejo de unión)
   public boolean E(AFN afn){
       if(T(afn)){
           if(Ep(afn)){
               return true;
           }
       }
       System.out.println("Expresion no valida.");
       return false;
   }
   
   //Ep -> |T EP| EPSILON
   public boolean Ep(AFN afn){
       int token = L.yylex();
       System.out.println("Ep(): token devuelto=" + token + " lexema='" + L.lexema + "'");
       if(token == Union){
           System.out.println("| encontrado");
           AFN f2 = new AFN();
           if(T(f2)){
                //Proceso para hacer la unión usando DatosAFN              
               int i1 = datos.getListaAFN().indexOf(afn);
                int i2 = datos.getListaAFN().indexOf(f2);

                if (i1 == -1 || i2 == -1) {
                    //datos.agregarAFN(afn);
                    //datos.agregarAFN(f2);
                    i1 = datos.getListaAFN().size() - 2;
                    i2 = datos.getListaAFN().size() - 1;
                }

                AFN unido = datos.unir(i1, i2);
                afn.edoInicial = unido.edoInicial;
                afn.edosAcept = unido.edosAcept;
                System.out.println("Unión '|' aplicada");

               if(Ep(afn)){ //Original: Ep(f2)
                   return true;
               }
           }
           System.out.println("No se encontraron uniones.");
           return false;
       }
       L.undoToken();
       return true;
   }
   
   // T -> F Tp EPSILON  (manejo de concatenaciones)
   public boolean T(AFN afn){
       if(F(afn)){
           if(Tp(afn)){
               return true;
           }
       }
       System.out.println("Expresion no valida 2.");
       return false;
   } 
   
   public boolean Tp(AFN afn){
       int token = L.yylex();
       System.out.println("Tp(): token devuelto=" + token + " lexema='" + L.lexema + "'");
       if(token == Concatenacion){
           System.out.println("& encontrado");
           AFN f2 = new AFN();
           if(F(f2)){
               //afn.concatenar(f2);
               int i1 = datos.getListaAFN().indexOf(afn);
                int i2 = datos.getListaAFN().indexOf(f2);

                if (i1 == -1 || i2 == -1) {
                    //datos.agregarAFN(afn);
                    //datos.agregarAFN(f2);
                    i1 = datos.getListaAFN().size() - 2;
                    i2 = datos.getListaAFN().size() - 1;
                }

                AFN concatenado = datos.concatenar(i1, i2);
                afn.edoInicial = concatenado.edoInicial;
                afn.edosAcept = concatenado.edosAcept;
                System.out.println("Concatenacion & aplicada");

               if(Tp(afn)){ //Original: Tp(f2)
                   return true;
               }
           }
           System.out.println("No se econtraron concatenaciones.");
           return false;
       }
       L.undoToken();
       return true;
   }
   
   // F -> Z Fp (manejo de operadores)
   public boolean F(AFN afn){
       if(Z(afn)){
           if(Fp(afn)){
               return true;
           }
       }
       System.out.println("Operadores de expresion erroneos.");
       return false;
   }
   
   //aplica los operadores que encuentre Fp -> +Fp | *Fp | ?Fp | EPSILON
   public boolean Fp(AFN afn){
       int token = L.yylex();
       int index = datos.getListaAFN().indexOf(afn);
       
       switch (token){
           case CPos:
               System.out.println("+ encontrado");
                if (index == -1) {
                    //datos.agregarAFN(afn);
                    index = datos.getListaAFN().size() - 1;
                }

                AFN pos = datos.cerrPos(index);
                afn.edoInicial = pos.edoInicial;
                afn.edosAcept = pos.edosAcept;
                System.out.println("cerradura + aplicada");

               return Fp(afn);//Si encuentra + busca más operadores
               //break;
           case CK:
               System.out.println("* encontrado");
                if (index == -1) {
                    //datos.agregarAFN(afn);
                    index = datos.getListaAFN().size() - 1;
                }

                AFN kle = datos.cerraduraKleene(index);
                afn.edoInicial = kle.edoInicial;
                afn.edosAcept = kle.edosAcept;
                System.out.println("cerradura * aplicada");

               return Fp(afn);//Si encuentra * busca más operadores
               //break;
           case CO:
               System.out.println("? encontrado");
                if (index == -1) {
                    //datos.agregarAFN(afn);
                    index = datos.getListaAFN().size() - 1;
                }

                AFN opc = datos.cerrOpc(index);
                afn.edoInicial = opc.edoInicial;
                afn.edosAcept = opc.edosAcept;
                System.out.println("cerradura ? aplicada");

               return Fp(afn);//Si encuenta ? busca más operadores 
               //break;
           default:
               L.undoToken();
               return true;//Ya no hay operadores 
               //break;
       }
       
       //return Fp(afn);  //busca mas operadores
   }
   
   // Z -> [E] | simb | (a-z)
   public boolean Z(AFN afn){
       int token = L.yylex();
       
       switch(token){
           case CI: // [
               System.out.println("[ encontrado");
               if(E(afn)){
                   System.out.println("analisis de E(afn)");
                   token = L.yylex();
                   if(token == CD){ // ]
                       System.out.println("] encontrado");
                       return true;
                   }
               }
               //System.out.println("Expresión no valida dentro de []");
               L.undoToken();
               return false;
               
           case Simb:
               char simb = L.lexema.charAt(0);
                if(simb == Gi){ // \
                    System.out.println("DInvert encontrado");
                    simb = L.lexema.charAt(1);
                }

                AFN basico = datos.CrearBasico(simb);  

                afn.edoInicial = basico.edoInicial;
                afn.edosAcept = basico.edosAcept;
                return true;
               
           case PI: // (
               char max, min;
               token = L.yylex();
               if(token == Simb){
                   min = L.lexema.charAt(0);
                   token = L.yylex();
                   if(token == Guion){ // -
                       token = L.yylex();
                       if(token == Simb){
                           max = L.lexema.charAt(0);
                           token = L.yylex();
                           if(token == PD){ // )
                               if(min > max){
                                   System.out.println("Rango incorrecto.");
                                   return false;
                               }
                               //afn.crearBasico(min, min);
                               AFN rango = datos.CrearBasico2(min, max);
                               afn.edoInicial = rango.edoInicial;
                               afn.edosAcept = rango.edosAcept;
                               return true;
                           }
                       }
                   }
               }
               System.out.println("Error: Paréntesis mal balanceados o expresión interna inválida.");
               return false;
           
               
           default: //en caso de que no haya devuelve false
               L.undoToken();
               return false;
       }
   }
   
}
