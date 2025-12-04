package Controllers;

import Models.AFN;
import Models.Calculadora;
import Models.ExpReg;
import Views.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import javax.swing.JFileChooser;
//import Models.*;
import javax.swing.JOptionPane;

public class Controller implements ActionListener {
    //primero se encapsulan las variables 
    private DatosAFN datos;
    private DatosAFD datosAFD;
    private Principal mainpage;
    private CrearBasico creaBasico;
    private CrearBasico2 creaBasico2;
    private Unir unir;
    private Concatenar conc;
    private CerrK cK;
    private CerrO cO;
    private CerrP cP;
    private ShowAFN safn;
    private AFD afd;
    private AnaLex al;
    private ERtoAFN er;
    private GdG gg;
    private LL1 ll;
    private LR0 lr;
    private Calc c;
    private Calculadora calculadora;
    private AFN afn;
    /*
    private DatosAFN datos;*/
    
    
    //Constructor
    public Controller() {
        datos = new DatosAFN();
        datosAFD = new DatosAFD();
        mainpage = new Principal();
        creaBasico = new CrearBasico();
        creaBasico2 = new CrearBasico2();
        unir =new Unir(datos);
        conc = new Concatenar(datos);
        cK = new CerrK(datos);
        cO = new CerrO(datos);
        cP = new CerrP(datos);
        safn = new ShowAFN();
        afd = new AFD();
        al = new AnaLex();
        er = new ERtoAFN();
        gg = new GdG();
        ll = new LL1();
        lr = new LR0();
        c = new Calc();
                
        //Apartado para poner los botones en escuha en la pagina principal (ponerles una acción)
        this.mainpage.CB_btn.addActionListener(this);
        this.mainpage.CB2_btn.addActionListener(this);
        this.mainpage.MAFN_btn.addActionListener(this);
        this.mainpage.U_btn.addActionListener(this);
        this.mainpage.Con_btn.addActionListener(this);
        this.mainpage.CK_btn.addActionListener(this);
        this.mainpage.CO_btn.addActionListener(this);
        this.mainpage.CP_btn.addActionListener(this);
        this.mainpage.AFD_btn.addActionListener(this);
        this.mainpage.AL_btn.addActionListener(this);
        this.mainpage.ER_btn.addActionListener(this);
        this.mainpage.GdG_btn.addActionListener(this);
        this.mainpage.LL1_btn.addActionListener(this);
        this.mainpage.LR0_btn.addActionListener(this);
        this.mainpage.Ca_btn.addActionListener(this);
        
        //Listener de los botones de las vistas SetController
        this.creaBasico.setController(this);
        this.creaBasico2.setController(this);
        this.safn.setControlador(this);
        this.unir.setController(this);
        this.conc.setController(this);
        this.cO.setController(this);
        this.cP.setController(this);
        this.cK.setController(this);
        this.afd.setController(this);
        this.al.setController(this);
        this.er.setController(this);
        this.gg.setController(this);
        this.ll.setController(this);
        this.lr.setController(this);
        this.c.setController(this);
        

        mainpage.setVisible(true);
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        //acción para el botón de Crear Básico
        System.out.println("Botón presionado");
        if(e.getSource()==mainpage.CB_btn){
            //CrearBasico ventana = new CrearBasico(this.datos);
            mainpage.setVisible(false);
            creaBasico.setVisible(true);
                      
        }else if(e.getSource() == mainpage.CB2_btn){
            mainpage.setVisible(false);
            creaBasico2.setVisible(true);
                        
        }else if(e.getSource()== mainpage.U_btn){
            mainpage.setVisible(false);
            unir.cargarAFN(datos.getListaAFN());
            unir.setVisible(true);
                        
        }else if(e.getSource()== mainpage.Con_btn){
            mainpage.setVisible(false);
            conc.cargarAFN(datos.getListaAFN());
            conc.setVisible(true);
                        
        }else if(e.getSource()== mainpage.CO_btn){
            mainpage.setVisible(false);
            cO.cargarAFN(datos.getListaAFN());
            cO.setVisible(true);
                        
        }else if(e.getSource()== mainpage.CP_btn){
            mainpage.setVisible(false);
            cP.cargarAFN(datos.getListaAFN());
            cP.setVisible(true);
                        
        }else if(e.getSource()== mainpage.CK_btn){
            mainpage.setVisible(false);
            cK.cargarAFN(datos.getListaAFN());
            cK.setVisible(true);
        
        }else if(e.getSource()== mainpage.MAFN_btn){
            mainpage.setVisible(false);
            safn.actualizarTabla(datos.getListaAFN());
            System.out.println(datos.getListaAFN());
            safn.setVisible(true);
            
        }else if(e.getSource() == mainpage.AL_btn){
            mainpage.setVisible(false);
            al.setVisible(true);
            
        }else if(e.getSource() == mainpage.AFD_btn){
            mainpage.setVisible(false);
            afd.llenarTablaAFN(datos);
            afd.setVisible(true);
        
        }else if(e.getSource() == mainpage.ER_btn){
            mainpage.setVisible(false);
            er.setVisible(true);
        
        }else if(e.getSource() == mainpage.GdG_btn){
            mainpage.setVisible(false);
            gg.setVisible(true);
            
        }else if(e.getSource() == mainpage.LL1_btn){
            mainpage.setVisible(false);
            ll.setVisible(true);
            
        }else if(e.getSource() == mainpage.LR0_btn){
            mainpage.setVisible(false);
            lr.setVisible(true);
                        
        }else if(e.getSource() == mainpage.Ca_btn){
            mainpage.setVisible(false);
            c.setVisible(true);
            
        

        //Espacio para controlar las acciones de los botones de las vista CrearBasico
        }else if(e.getSource() == creaBasico.OutCB_btn){
            creaBasico.setVisible(false);
            mainpage.setVisible(true);
        }else if(e.getSource() == creaBasico.OKCB_btn){
            String c = creaBasico.txt_charb.getText();
            
            if(c.isEmpty()){
                JOptionPane.showMessageDialog(null, "No puedes dejar el espacio vacío");
                return;
            }
            
            // Tomar el primer carácter de cada campo
            char c1 = c.charAt(0);
            datos.CrearBasico(c1);
        
            JOptionPane.showMessageDialog(null, "AFN creado y guardado correctamente.");
        
        
        //Espacio para controlar los botones de la vista CrearBasico2
        }else if(e.getSource() == creaBasico2.OutCB2_btn){
            creaBasico.setVisible(false);
            mainpage.setVisible(true);
        }else if(e.getSource() == creaBasico2.OKCB2_btn){
            String s = creaBasico2.txt_charS.getText();
            String i = creaBasico2.txt_charI.getText();
                        
            if (s.isEmpty() || i.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Completa ambos campos.");
                return;
            }
        
            // Tomar el primer carácter de cada campo
            char c1 = s.charAt(0);
            char c2 = i.charAt(0);

            datos.CrearBasico2(c1, c2);

            JOptionPane.showMessageDialog(null, "AFN creado y guardado correctamente.");
        
        //Espacio para controlar el botón de salida de ShowAFNs
        }else if(e.getSource() == safn.OutAL_btn){
            safn.setVisible(false);
            mainpage.setVisible(true);
            
            
        //Espacio para controlar el botón de salida de Unir
        }else if(e.getSource() == unir.OutU_btn){
            safn.setVisible(false);
            mainpage.setVisible(true);
        }else if(e.getSource() == unir.OKU_btn){
            int i1 = unir.U1_box.getSelectedIndex();
            int i2 = unir.U2_box.getSelectedIndex();
            
            if(i1 == -1 || i2 ==-1){
                JOptionPane.showMessageDialog(null, "AFNs no válidos.");
            }else{
                if(i1 == i2){
                    JOptionPane.showMessageDialog(null, "No puedes unir el mismo AFN.");
                    return;
                }
            
                datos.unir(i1, i2);
                JOptionPane.showMessageDialog(null, "AFN creado y guardado correctamente.");
            }
                    
            
        //Espacio para controlar el botón de salida de Concatenar
        }else if(e.getSource() == conc.OutCon_btn){
            conc.setVisible(false);
            mainpage.setVisible(true);
        }else if(e.getSource() == conc.OKCon_btn){
            boolean isConcatenating = false;
            if (isConcatenating) return; // Evita doble ejecución
                isConcatenating = true;
            //int raw_i1 = conc.Con1_box.getSelectedIndex();
            //int raw_i2 = conc.Con2_box.getSelectedIndex();
            int i1 = conc.Con1_box.getSelectedIndex();
            int i2 = conc.Con2_box.getSelectedIndex();
            
            /*if(i1 == 0 || i2 == 0){ // Si el placeholder es el índice 0
                JOptionPane.showMessageDialog(null, "Debes seleccionar AFNs válidos.");
                return; // Añadir return para evitar continuar
            }*/
            
            if(i1 == -1 || i2 == -1){ // Si el placeholder es el índice -1
                JOptionPane.showMessageDialog(null, "Debes seleccionar AFNs válidos.");
                return; // Añadir return para evitar continuar
            }
            
            if(i1 < 0 || i2 < 0){ // Verifica que ambos índices sean válidos después del ajuste
                JOptionPane.showMessageDialog(null, "AFNs no válidos (Error de índice).");
            }else{
                if(i1 == i2){
                    JOptionPane.showMessageDialog(null, "No puedes concatenar el mismo AFN.");
                    return;
                }
            
                datos.concatenar(i1, i2); // Línea 217
                JOptionPane.showMessageDialog(null, "AFN creado y guardado correctamente.");
            }
            
            
        //Espacio para controlar el botón de salida de Cerradura +
        }else if(e.getSource() == cP.OutP_btn){
            cP.setVisible(false);
            mainpage.setVisible(true);
        }else if(e.getSource() == cP.OKCP_btn){
            int i = cP.CP_box.getSelectedIndex();
            if (i == -1) {
                JOptionPane.showMessageDialog(null, "Selecciona un AFN.");
                return;
            }else{
                datos.cerrPos(i);
                JOptionPane.showMessageDialog(null, "Cerradura + aplicada correctamente.");
            }
            
        //Espacio para controlar el botón de salida de Cerradura *
        }else if(e.getSource() == cK.OutK_btn){
            cK.setVisible(false);
            mainpage.setVisible(true);
        }else if(e.getSource() == cK.OKK_btn){
            //int raw_i = cK.K_box.getSelectedIndex();
            int i = cK.K_box.getSelectedIndex();
            if (i == -1) {
                JOptionPane.showMessageDialog(null, "Selecciona un AFN.");
                return;
            }else{
                //System.out.println("raw_i = " + raw_i + ", i = " + i);
                //System.out.println("Tamaño listaAFN: " + datos.getListaAFN().size());
                datos.cerraduraKleene(i);
                JOptionPane.showMessageDialog(null, "Cerradura * aplicada correctamente.");
            }
            
        //Espacio para controlar el botón de salida de Cerradura ?
        }else if(e.getSource() == cO.OutO_btn){
            cO.setVisible(false);
            mainpage.setVisible(true);
        }else if(e.getSource() == cO.OKO_btn){    
            int i = cO.Ep_box.getSelectedIndex();
            if (i == -1) {
                JOptionPane.showMessageDialog(null, "Selecciona un AFN.");
                return;
            }else{
                datos.cerrOpc(i);
                JOptionPane.showMessageDialog(null, "Cerradura ? aplicada correctamente.");
            }
        
        
        //Espacio para controlar los botones de la vista AFD
        }else if(e.getSource() == afd.OutAFD_btn){
            afd.setVisible(false);
            mainpage.setVisible(true);
        }else if(e.getSource() == afd.OKAFD_btn){
            
            AbstractMap.SimpleEntry<List<AFN>, List<String>> seleccionados = afd.obtenerSeleccion(datos);

            //️ VALIDAR SELECCIÓN
            if (seleccionados == null) {
                JOptionPane.showMessageDialog(null, "No se seleccionaron AFNs válidos.");
                return;
            }

            List<AFN> listaSeleccionados = seleccionados.getKey();
            List<String> tokens = seleccionados.getValue();

            if (listaSeleccionados.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debe seleccionar al menos un AFN.");
                return;
            }

            Models.AFD afdFinal = datosAFD.convertirAFNsSeleccionados(listaSeleccionados, tokens);

            if (afdFinal == null) {
                JOptionPane.showMessageDialog(null, "No se pudo generar el AFD (AFN vacío o inválido).");
                return;
            }

            //Si todo está bien, guardar
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String ruta = fc.getSelectedFile().getAbsolutePath();
                boolean guardado = afdFinal.saveAFD(ruta);
                if (guardado)
                    JOptionPane.showMessageDialog(null, "AFD guardado correctamente en:\n" + ruta);
                else
                    JOptionPane.showMessageDialog(null, "Error al guardar el AFD.");
            }

        
        //Espacio para controlar los botones de la vista AnaLex
        }else if(e.getSource() == al.OutAL_btn){
            al.setVisible(false);
            mainpage.setVisible(true);
        }else if(e.getSource() == al.OKAL_btn){
            String sigma = al.AL_txt.getText().trim();
            String ruta = al.txtRuta.getText();
            if(sigma.isEmpty()){
                JOptionPane.showMessageDialog(null, "Ingrese una expresión regular.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (ruta == null || ruta.isEmpty()){
                JOptionPane.showMessageDialog(null, "Cargue primero un archivo", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(datosAFD.cargarAFD(ruta)){
                List<String[]> resultados = datosAFD.analizarExpresion(sigma, ruta);
                
                al.limpiarTabla();
                for(String[] fila : resultados){
                    al.agregarFilaTabla(fila);
                }
            }else{
                JOptionPane.showMessageDialog(null, "No se pudo cargar el AFD desde el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            
        
        
        //Espacio para controlar los botones de ERtoAFN
        }else if(e.getSource() == er.OutER_btn){
            er.setVisible(false);
            mainpage.setVisible(true);
        }else if(e.getSource() == er.OKER_btn){
            String sigma = er.ER_txt.getText();
            String ruta = er.txtRuta.getText();

            // Validaciones UI
            if (sigma == null || sigma.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese una expresión regular", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (ruta == null || ruta.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Cargue primero un archivo AFD", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Crear conversor
                ExpReg conversor = new ExpReg(sigma.trim(), ruta.trim(), datos);

                // Ejecutar conversión
                boolean ok = conversor.Conversion();

                if (ok && conversor.resultado != null) {
                    afn = conversor.resultado;
                    JOptionPane.showMessageDialog(null, "Conversión exitosa. Lexema generado: " + sigma);
                } else {
                    JOptionPane.showMessageDialog(null, "La conversión falló. Revisa operadores o estructura de [ ]", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                ex.printStackTrace(); 
                JOptionPane.showMessageDialog(null, "Error interno en conversión:\n" + ex.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                System.err.println("Error real:\n" + ex);
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        
            
            
            //Espacio para controlar los botones de LL1
        }else if(e.getSource() == ll.OutLL1_btn){
            ll.setVisible(false);
            mainpage.setVisible(true);
        }else if(e.getSource() == ll.OKLL1_btn){
            String ruta = ll.txtRuta.getText();
            if (ruta == null || ruta.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Cargue primero un archivo", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                java.nio.file.Path p = java.nio.file.Paths.get(ruta);
                if (!java.nio.file.Files.exists(p)) {
                    JOptionPane.showMessageDialog(null, "Archivo no encontrado:\n" + ruta, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Cargar gramática y calcular FIRST/FOLLOW
                Models.Grammar gr = new Models.Grammar();
                gr.loadFromFile(ruta);
                java.util.Map<String, java.util.Set<String>> F = gr.computeFirst();
                java.util.Map<String, java.util.Set<String>> FO = gr.computeFollow();

                // Poblar vista LL1
                List<String> firstRows = new java.util.ArrayList<>();
                List<String[]> followRows = new java.util.ArrayList<>();
                for (String A : gr.nonTerminals()) {
                    java.util.Set<String> s = F.getOrDefault(A, java.util.Collections.emptySet());
                    String joined = String.join(", ", s);
                    firstRows.add(A + " : {" + joined + "}");

                    java.util.Set<String> fo = FO.getOrDefault(A, java.util.Collections.emptySet());
                    String joinedFo = String.join(", ", fo);
                    followRows.add(new String[]{A, joinedFo});
                }

                ll.setGrammarText(new String(java.nio.file.Files.readAllBytes(p)));
                ll.setFirst(firstRows);
                ll.setFollow(followRows);
                // Construir tabla LL(1)
                Models.LL1Parser llp = new Models.LL1Parser(gr);
                boolean isLL1 = llp.build();
                String tableStr = llp.tableToString();
                String fileContent = new String(java.nio.file.Files.readAllBytes(p));
                ll.setResultText(fileContent + "\n\n" + tableStr);

                if(isLL1) JOptionPane.showMessageDialog(null, "Gramática es LL(1). Tabla mostrada en la vista.");
                else JOptionPane.showMessageDialog(null, "La gramática NO es LL(1). Revise los conflictos en la tabla mostrada.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error al procesar la gramática:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            //Espacio para controlar los botones de LR0
        }else if(e.getSource() == lr.OutLR_btn){
            lr.setVisible(false);
            mainpage.setVisible(true);
        }else if(e.getSource() == lr.OKLR_btn){
            String ruta = lr.txtRuta.getText();
            if (ruta == null || ruta.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Cargue primero un archivo", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                java.nio.file.Path p = java.nio.file.Paths.get(ruta);
                if (!java.nio.file.Files.exists(p)) {
                    JOptionPane.showMessageDialog(null, "Archivo no encontrado:\n" + ruta, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Cargar gramática y calcular FIRST/FOLLOW (se muestran en LR0 también)
                Models.Grammar gr = new Models.Grammar();
                gr.loadFromFile(ruta);
                java.util.Map<String, java.util.Set<String>> F = gr.computeFirst();
                java.util.Map<String, java.util.Set<String>> FO = gr.computeFollow();

                List<String> firstRows = new java.util.ArrayList<>();
                List<String[]> followRows = new java.util.ArrayList<>();
                for (String A : gr.nonTerminals()) {
                    java.util.Set<String> s = F.getOrDefault(A, java.util.Collections.emptySet());
                    String joined = String.join(", ", s);
                    firstRows.add(A + " : {" + joined + "}");

                    java.util.Set<String> fo = FO.getOrDefault(A, java.util.Collections.emptySet());
                    String joinedFo = String.join(", ", fo);
                    followRows.add(new String[]{A, joinedFo});
                }

                lr.setGrammarText(new String(java.nio.file.Files.readAllBytes(p)));
                lr.setFirst(firstRows);
                lr.setFollow(followRows);
                // Construir colección canónica LR(0)
                Models.LR0Parser lrparser = new Models.LR0Parser(gr);
                Models.LR0Parser.CanonicalResult cres = lrparser.buildCanonicalCollection();

                StringBuilder out = new StringBuilder();
                out.append("LR(0) Canonical Collection. States: ").append(cres.states.size()).append("\n\n");
                for (int i = 0; i < cres.states.size(); i++) {
                    out.append("State ").append(i).append("\n");
                    for (Models.LR0Parser.Item it : cres.states.get(i).items) {
                        out.append("  ").append(it.toString()).append("\n");
                    }
                    out.append("\n");
                }
                out.append("Transitions:\n");
                for (java.util.Map.Entry<Integer, java.util.Map<String, Integer>> entr : cres.transitions.entrySet()) {
                    int from = entr.getKey();
                    for (java.util.Map.Entry<String, Integer> t : entr.getValue().entrySet()) {
                        out.append("  ").append(from).append(" --[").append(t.getKey()).append("]--> ").append(t.getValue()).append("\n");
                    }
                }
                if (!cres.conflicts.isEmpty()) {
                    out.append("\nConflicts detected:\n");
                    for (String cstr : cres.conflicts) out.append(cstr).append("\n");
                } else {
                    out.append("\nNo LR(0) conflicts detected.\n");
                }

                lr.setResultText(out.toString());
                JOptionPane.showMessageDialog(null, "Construcción LR(0) completada. Revise la vista LR(0) para detalles.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error al procesar la gramática:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        
        
        //Espacio para controlar los botones de la calculadora
        }else if(e.getSource() == c.OutCalc_btn){
            c.setVisible(false);
            mainpage.setVisible(true);
        }else if(e.getSource() == c.OKCalc_btn){
            String sigma = c.ExA_txt.getText().trim();
            String ruta = c.txtRuta.getText();
                        
            //Validaciones UI
            if(sigma.isEmpty()){
                JOptionPane.showMessageDialog(null, "Ingrese una expresión.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (ruta == null || ruta.isEmpty()){
                JOptionPane.showMessageDialog(null, "Cargue primero un archivo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try{
                //Crear calculadora
                calculadora = new Calculadora (sigma, ruta.trim());
                
                //Ejecutar calculadora
                boolean ok = calculadora.iniEval(); 
                
                if(ok){
                    JOptionPane.showMessageDialog(null, "Expresión válida y evaluada.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    c.setResult(String.valueOf(calculadora.result));
                    c.setPostF(calculadora.PostF);
                }else{
                    JOptionPane.showMessageDialog(null, "Error al evaluar la expresión.", "Adventencia", JOptionPane.WARNING_MESSAGE);
                }
            }catch(Exception el){
                JOptionPane.showMessageDialog(null, "Ocurrio un error." + el.getMessage());
            }
            
            
            
        
        
        //Espacio para controlar los botones de la Gramática de gramáticas
        }else if(e.getSource() == gg.OutGG_btn){
            gg.setVisible(false);
            mainpage.setVisible(true);
        }else if(e.getSource() == gg.OKGG_btn){
            String ruta = gg.txtRuta.getText();
            String cadena = gg.AL_txt.getText();

            if (ruta == null || ruta.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Cargue primero un archivo de gramática", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (cadena == null || cadena.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese la cadena a analizar sintácticamente", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                java.nio.file.Path p = java.nio.file.Paths.get(ruta);
                if (!java.nio.file.Files.exists(p)) {
                    JOptionPane.showMessageDialog(null, "Archivo no encontrado:\n" + ruta, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Models.Grammar gr = new Models.Grammar();
                gr.loadFromFile(ruta);
                java.util.Map<String, java.util.Set<String>> F = gr.computeFirst();
                java.util.Map<String, java.util.Set<String>> FO = gr.computeFollow();

                // preparar símbolos: marcar terminal/nonterminal
                List<String[]> simbRows = new java.util.ArrayList<>();
                for(String t : gr.Vt){
                    simbRows.add(new String[]{t, "terminal"});
                }
                for(String nt : gr.Vn){
                    simbRows.add(new String[]{nt, "non-terminal"});
                }

                // non-terminals list
                List<String> nts = new java.util.ArrayList<>(gr.Vn);

                // first and follow as simple lists
                List<String> firstList = new java.util.ArrayList<>();
                List<String> followList = new java.util.ArrayList<>();
                for(String A : gr.nonTerminals()){
                    firstList.add(A + " : {" + String.join(", ", F.getOrDefault(A, java.util.Collections.emptySet())) + "}");
                    followList.add(A + " : {" + String.join(", ", FO.getOrDefault(A, java.util.Collections.emptySet())) + "}");
                }

                gg.setInputString(cadena);
                gg.setSymbols(simbRows);
                gg.setNonTerminals(nts);
                gg.setFirst(firstList);
                gg.setFollow(followList);

                    // Intentar construir tabla LL(1) y, si es LL(1), analizar la cadena dada
                    Models.LL1Parser llp = new Models.LL1Parser(gr);
                    boolean isLL1 = llp.build();
                    String tableStr = llp.tableToString();
                    StringBuilder resOut = new StringBuilder();
                    resOut.append("FIRST/FOLLOW y símbolos listados.\n\n");
                    resOut.append(tableStr).append("\n");
                    if(isLL1){
                        boolean parsed = llp.parseInput(cadena);
                        resOut.append(parsed ? "La cadena fue aceptada por el parser LL(1)." : "La cadena NO fue aceptada por el parser LL(1).");
                    } else {
                        resOut.append("No se puede analizar con LL(1) (conflictos en la tabla).\n");
                    }

                    gg.setResultText(resOut.toString());
                    JOptionPane.showMessageDialog(null, "Gramática procesada y datos mostrados en GdG.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error al procesar la gramática:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
