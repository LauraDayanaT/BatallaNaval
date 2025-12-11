package com.batallanaval.batallanaval.utils;

import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Glow;
import javafx.scene.Group;

/**
 * Utilidades para crear figuras 2D en JavaFX.
 * Cumple con el requisito de implementar figuras 2D para el juego.
 *
 * @author [Tu Nombre o nombres del grupo]
 * @version 1.2
 */
public class Figuras2DUtils {

    // ========== COLORES DEL JUEGO ==========

    public static final Color COLOR_AGUA = Color.rgb(30, 144, 255, 0.8);    // Azul Dodger
    public static final Color COLOR_TOCADO = Color.rgb(255, 69, 0, 0.8);    // Rojo Naranja
    public static final Color COLOR_HUNDIDO = Color.rgb(139, 0, 0, 0.8);    // Rojo Oscuro

    // Colores para diferentes tipos de barcos
    public static final Color COLOR_PORTAVIONES = Color.rgb(255, 140, 0);         // Naranja Oscuro
    public static final Color COLOR_DESTRUCTOR = Color.rgb(34, 139, 34);          // Verde Bosque
    public static final Color COLOR_FRAGATA = Color.rgb(0, 0, 139);               // Azul Oscuro
    public static final Color COLOR_SUBMARINO = Color.rgb(128, 128, 128);         // Gris

    // Colores claros para variantes
    public static final Color COLOR_PORTAVIONES_CLARO = Color.rgb(255, 165, 0);   // Naranja
    public static final Color COLOR_DESTRUCTOR_CLARO = Color.rgb(50, 205, 50);    // Verde Lima
    public static final Color COLOR_FRAGATA_CLARO = Color.rgb(30, 144, 255);      // Azul Dodger
    public static final Color COLOR_SUBMARINO_CLARO = Color.rgb(169, 169, 169);   // Gris Claro

    public static final Color COLOR_TABLERO = Color.rgb(135, 206, 235, 0.7);      // Celeste
    public static final Color COLOR_BORDE = Color.rgb(70, 130, 180);              // Azul Acero

    // ========== EFECTOS ==========

    private static DropShadow crearSombraExterna() {
        DropShadow sombra = new DropShadow();
        sombra.setColor(Color.rgb(0, 0, 0, 0.5));
        sombra.setRadius(5);
        sombra.setOffsetX(2);
        sombra.setOffsetY(2);
        return sombra;
    }

    private static InnerShadow crearSombraInterna() {
        InnerShadow sombra = new InnerShadow();
        sombra.setColor(Color.rgb(255, 255, 255, 0.3));
        sombra.setRadius(10);
        sombra.setOffsetX(0);
        sombra.setOffsetY(0);
        return sombra;
    }

    private static Glow crearBrillo() {
        Glow brillo = new Glow();
        brillo.setLevel(0.3);
        return brillo;
    }

    // ========== FIGURAS PARA DISPAROS ==========

    /**
     * Crea un círculo para representar AGUA (disparo fallido).
     *
     * @param tamaño Tamaño del círculo
     * @return Circle configurado
     */
    public static Group crearCirculoAgua(double tamaño) {
        Circle circulo = new Circle(tamaño / 2);
        circulo.setFill(COLOR_AGUA);
        circulo.setStroke(Color.BLUE);
        circulo.setStrokeWidth(2);
        circulo.setEffect(crearSombraExterna());

        // Agregar onda de agua (círculo concéntrico)
        Circle onda = new Circle(tamaño / 2 - 2);
        onda.setFill(Color.TRANSPARENT);
        onda.setStroke(Color.rgb(173, 216, 230, 0.7)); // Azul claro
        onda.setStrokeWidth(1);
        onda.setStrokeType(StrokeType.OUTSIDE);

        Group grupo = new Group(circulo, onda);
        return grupo;
    }

    /**
     * Crea un círculo para representar TOCADO (barco dañado).
     *
     * @param tamaño Tamaño del círculo
     * @return Group con círculo y efecto de fuego
     */
    public static Group crearCirculoTocado(double tamaño) {
        Circle circulo = new Circle(tamaño / 2);
        circulo.setFill(COLOR_TOCADO);
        circulo.setStroke(Color.RED);
        circulo.setStrokeWidth(2);
        circulo.setEffect(crearBrillo());

        // Agregar efecto de fuego (triángulo interior)
        Polygon fuego = new Polygon();
        fuego.getPoints().addAll(
                tamaño * 0.3, tamaño * 0.7,
                tamaño * 0.5, tamaño * 0.3,
                tamaño * 0.7, tamaño * 0.7
        );
        fuego.setFill(Color.ORANGE);
        fuego.setStroke(Color.YELLOW);
        fuego.setStrokeWidth(1);

        return new Group(circulo, fuego);
    }

    /**
     * Crea un círculo para representar HUNDIDO (barco destruido).
     *
     * @param tamaño Tamaño del círculo
     * @return Group con círculo y cruz de destrucción
     */
    public static Group crearCirculoHundido(double tamaño) {
        Circle circulo = new Circle(tamaño / 2);
        circulo.setFill(COLOR_HUNDIDO);
        circulo.setStroke(Color.DARKRED);
        circulo.setStrokeWidth(3);
        circulo.setEffect(crearSombraInterna());

        // Agregar cruz de destrucción
        Line cruz1 = new Line(tamaño * 0.2, tamaño * 0.2, tamaño * 0.8, tamaño * 0.8);
        Line cruz2 = new Line(tamaño * 0.8, tamaño * 0.2, tamaño * 0.2, tamaño * 0.8);
        cruz1.setStroke(Color.BLACK);
        cruz1.setStrokeWidth(2);
        cruz2.setStroke(Color.BLACK);
        cruz2.setStrokeWidth(2);

        return new Group(circulo, cruz1, cruz2);
    }

    // ========== FIGURAS PARA BARCOS ==========

    /**
     * Crea un rectángulo para representar un barco.
     *
     * @param ancho Ancho del barco
     * @param alto Alto del barco
     * @param horizontal true si es horizontal, false si vertical
     * @param tipoBarco Tipo de barco (PORTAAVIONES, DESTRUCTOR, FRAGATA, SUBMARINO)
     * @return Rectangle configurado
     */
    public static Group crearBarcoFigura(double ancho, double alto, boolean horizontal, String tipoBarco) {
        Rectangle barco = new Rectangle(ancho, alto);

        // Seleccionar color según el tipo de barco
        Color colorPrincipal, colorSecundario;

        switch (tipoBarco.toUpperCase()) {
            case "DESTRUCTOR":
                colorPrincipal = COLOR_DESTRUCTOR;
                colorSecundario = COLOR_DESTRUCTOR_CLARO;
                break;
            case "FRAGATA":
                colorPrincipal = COLOR_FRAGATA;
                colorSecundario = COLOR_FRAGATA_CLARO;
                break;
            case "SUBMARINO":
                colorPrincipal = COLOR_SUBMARINO;
                colorSecundario = COLOR_SUBMARINO_CLARO;
                break;
            default:
                colorPrincipal = COLOR_PORTAVIONES;
                colorSecundario = COLOR_PORTAVIONES_CLARO;
        }

        // Usar colores según orientación
        if (horizontal) {
            barco.setFill(colorPrincipal);
            barco.setStroke(colorPrincipal.darker());
        } else {
            barco.setFill(colorSecundario);
            barco.setStroke(colorSecundario.darker());
        }

        barco.setStrokeWidth(2);
        barco.setArcWidth(10);
        barco.setArcHeight(10); // Esquinas redondeadas
        barco.setEffect(crearSombraExterna());

        // Agregar detalles según el tipo de barco
        Group grupo = new Group(barco);

        // Detalles específicos para cada tipo
        agregarDetallesBarco(grupo, ancho, alto, tipoBarco);

        return grupo;
    }

    /**
     * Agrega detalles específicos según el tipo de barco.
     */
    private static void agregarDetallesBarco(Group grupo, double ancho, double alto, String tipoBarco) {
        switch (tipoBarco.toUpperCase()) {
            case "PORTAVIONES":
                // Ventanas grandes para portaaviones
                for (int i = 0; i < 3; i++) {
                    Rectangle ventana = new Rectangle(ancho * 0.08, alto * 0.4);
                    ventana.setFill(Color.rgb(255, 255, 255, 0.6));
                    ventana.setStroke(Color.rgb(200, 200, 200, 0.8));
                    ventana.setStrokeWidth(1);
                    ventana.setX(ancho * (0.2 + i * 0.3));
                    ventana.setY(alto * 0.3);
                    grupo.getChildren().add(ventana);
                }
                break;

            case "DESTRUCTOR":
                // Cañones para destructor
                for (int i = 0; i < 2; i++) {
                    Circle canon = new Circle(ancho * 0.04);
                    canon.setFill(Color.rgb(50, 50, 50));
                    canon.setStroke(Color.rgb(30, 30, 30));
                    canon.setCenterX(ancho * (0.3 + i * 0.4));
                    canon.setCenterY(alto * 0.3);
                    grupo.getChildren().add(canon);
                }
                break;

            case "FRAGATA":
                // Ventanas pequeñas para fragata
                for (int i = 0; i < 4; i++) {
                    Circle ventana = new Circle(ancho * 0.03);
                    ventana.setFill(Color.rgb(173, 216, 230, 0.7)); // Azul claro
                    ventana.setStroke(Color.rgb(135, 206, 235));
                    ventana.setCenterX(ancho * (0.15 + i * 0.23));
                    ventana.setCenterY(alto * 0.5);
                    grupo.getChildren().add(ventana);
                }
                break;

            case "SUBMARINO":
                // Ventana de observación para submarino
                Circle ventana = new Circle(ancho * 0.1);
                ventana.setFill(Color.rgb(0, 0, 139, 0.5)); // Azul oscuro semitransparente
                ventana.setStroke(Color.rgb(0, 0, 100));
                ventana.setCenterX(ancho * 0.5);
                ventana.setCenterY(alto * 0.5);
                grupo.getChildren().add(ventana);
                break;
        }
    }

    /**
     * Crea un barco especial (Portaaviones) con más detalles.
     */
    public static Group crearPortaaviones(double ancho, double alto) {
        // Cuerpo principal
        Rectangle cuerpo = new Rectangle(ancho * 0.8, alto);
        cuerpo.setFill(COLOR_PORTAVIONES); // Naranja Oscuro
        cuerpo.setStroke(COLOR_PORTAVIONES.darker());
        cuerpo.setStrokeWidth(2);
        cuerpo.setArcWidth(15);
        cuerpo.setArcHeight(15);
        cuerpo.setX(ancho * 0.1); // Centrado

        // Cubierta (parte superior)
        Rectangle cubierta = new Rectangle(ancho * 0.9, alto * 0.3);
        cubierta.setFill(COLOR_PORTAVIONES_CLARO); // Naranja
        cubierta.setStroke(COLOR_PORTAVIONES_CLARO.darker());
        cubierta.setStrokeWidth(1);
        cubierta.setX(ancho * 0.05);
        cubierta.setY(alto * 0.1);
        cubierta.setArcWidth(10);
        cubierta.setArcHeight(10);

        // Torre de control (círculo)
        Circle torre = new Circle(ancho * 0.1);
        torre.setFill(Color.rgb(105, 105, 105)); // Gris oscuro
        torre.setStroke(Color.rgb(64, 64, 64));
        torre.setStrokeWidth(1);
        torre.setCenterX(ancho * 0.5);
        torre.setCenterY(alto * 0.3);

        // Pista de aterrizaje (líneas)
        Line pista1 = new Line(ancho * 0.1, alto * 0.2, ancho * 0.9, alto * 0.2);
        Line pista2 = new Line(ancho * 0.1, alto * 0.25, ancho * 0.9, alto * 0.25);
        pista1.setStroke(Color.WHITE);
        pista1.setStrokeWidth(1);
        pista2.setStroke(Color.WHITE);
        pista2.setStrokeWidth(1);

        // Grupo todas las partes
        Group portaaviones = new Group(cuerpo, cubierta, torre, pista1, pista2);
        portaaviones.setEffect(crearSombraExterna());

        return portaaviones;
    }

    /**
     * Crea un submarino (forma ovalada).
     */
    public static Group crearSubmarino(double ancho, double alto) {
        // Forma ovalada (elipse)
        Ellipse submarino = new Ellipse(ancho * 0.4, alto * 0.4);
        submarino.setFill(COLOR_SUBMARINO); // Gris
        submarino.setStroke(COLOR_SUBMARINO.darker());
        submarino.setStrokeWidth(2);
        submarino.setCenterX(ancho * 0.5);
        submarino.setCenterY(alto * 0.5);

        // Torre del submarino
        Rectangle torre = new Rectangle(ancho * 0.15, alto * 0.6);
        torre.setFill(COLOR_SUBMARINO_CLARO); // Gris Claro
        torre.setStroke(COLOR_SUBMARINO_CLARO.darker());
        torre.setStrokeWidth(1);
        torre.setX(ancho * 0.425);
        torre.setY(alto * 0.2);
        torre.setArcWidth(5);
        torre.setArcHeight(5);

        // Ventana de observación
        Circle ventana = new Circle(ancho * 0.08);
        ventana.setFill(Color.rgb(0, 0, 139, 0.5)); // Azul oscuro semitransparente
        ventana.setStroke(Color.rgb(0, 0, 100));
        ventana.setCenterX(ancho * 0.5);
        ventana.setCenterY(alto * 0.5);

        // Periscopio (línea)
        Line periscopio = new Line();
        periscopio.setStartX(ancho * 0.5);
        periscopio.setStartY(alto * 0.2);
        periscopio.setEndX(ancho * 0.5);
        periscopio.setEndY(alto * 0.1);
        periscopio.setStroke(Color.BLACK);
        periscopio.setStrokeWidth(1.5);

        Group grupo = new Group(submarino, torre, ventana, periscopio);
        grupo.setEffect(crearSombraExterna());

        return grupo;
    }

    /**
     * Crea un destructor con detalles especiales.
     */
    public static Group crearDestructor(double ancho, double alto) {
        Group grupo = new Group();

        // Cuerpo principal
        Rectangle cuerpo = new Rectangle(ancho * 0.9, alto);
        cuerpo.setFill(COLOR_DESTRUCTOR); // Verde Bosque
        cuerpo.setStroke(COLOR_DESTRUCTOR.darker());
        cuerpo.setStrokeWidth(2);
        cuerpo.setArcWidth(12);
        cuerpo.setArcHeight(12);
        cuerpo.setX(ancho * 0.05);
        grupo.getChildren().add(cuerpo);

        // Superestructura
        Rectangle superestructura = new Rectangle(ancho * 0.5, alto * 0.7);
        superestructura.setFill(COLOR_DESTRUCTOR_CLARO); // Verde Lima
        superestructura.setStroke(COLOR_DESTRUCTOR_CLARO.darker());
        superestructura.setStrokeWidth(1);
        superestructura.setX(ancho * 0.25);
        superestructura.setY(alto * 0.15);
        superestructura.setArcWidth(8);
        superestructura.setArcHeight(8);
        grupo.getChildren().add(superestructura);

        // Cañones
        for (int i = 0; i < 2; i++) {
            Rectangle canon = new Rectangle(ancho * 0.08, alto * 0.4);
            canon.setFill(Color.rgb(50, 50, 50));
            canon.setStroke(Color.rgb(30, 30, 30));
            canon.setX(ancho * (0.15 + i * 0.7));
            canon.setY(alto * 0.3);
            canon.setArcWidth(3);
            canon.setArcHeight(3);
            grupo.getChildren().add(canon);
        }

        grupo.setEffect(crearSombraExterna());

        return grupo;
    }

    /**
     * Crea una fragata con detalles especiales.
     */
    public static Group crearFragata(double ancho, double alto) {
        Group grupo = new Group();

        // Cuerpo principal
        Rectangle cuerpo = new Rectangle(ancho * 0.85, alto);
        cuerpo.setFill(COLOR_FRAGATA); // Azul Oscuro
        cuerpo.setStroke(COLOR_FRAGATA.darker());
        cuerpo.setStrokeWidth(2);
        cuerpo.setArcWidth(8);
        cuerpo.setArcHeight(8);
        cuerpo.setX(ancho * 0.075);
        grupo.getChildren().add(cuerpo);

        // Cubierta
        Rectangle cubierta = new Rectangle(ancho * 0.7, alto * 0.6);
        cubierta.setFill(COLOR_FRAGATA_CLARO); // Azul Dodger
        cubierta.setStroke(COLOR_FRAGATA_CLARO.darker());
        cubierta.setStrokeWidth(1);
        cubierta.setX(ancho * 0.15);
        cubierta.setY(alto * 0.2);
        cubierta.setArcWidth(6);
        cubierta.setArcHeight(6);
        grupo.getChildren().add(cubierta);

        // Ventanas
        for (int i = 0; i < 3; i++) {
            Circle ventana = new Circle(ancho * 0.03);
            ventana.setFill(Color.rgb(173, 216, 230, 0.7)); // Azul claro
            ventana.setStroke(Color.rgb(135, 206, 235));
            ventana.setCenterX(ancho * (0.25 + i * 0.25));
            ventana.setCenterY(alto * 0.5);
            grupo.getChildren().add(ventana);
        }

        grupo.setEffect(crearSombraExterna());

        return grupo;
    }

    // ========== FIGURAS PARA ELEMENTOS DE JUEGO ==========

    /**
     * Crea una X para marcar disparo repetido.
     */
    public static Group crearMarcaRepetido(double tamaño) {
        Line linea1 = new Line(tamaño * 0.2, tamaño * 0.2, tamaño * 0.8, tamaño * 0.8);
        Line linea2 = new Line(tamaño * 0.8, tamaño * 0.2, tamaño * 0.2, tamaño * 0.8);

        linea1.setStroke(Color.GRAY);
        linea1.setStrokeWidth(3);
        linea1.setStrokeLineCap(StrokeLineCap.ROUND);

        linea2.setStroke(Color.GRAY);
        linea2.setStrokeWidth(3);
        linea2.setStrokeLineCap(StrokeLineCap.ROUND);

        Group grupo = new Group(linea1, linea2);
        grupo.setEffect(crearSombraExterna());

        return grupo;
    }

    /**
     * Crea una flecha para indicar orientación.
     */
    public static Polygon crearFlecha(double tamaño, boolean derecha) {
        Polygon flecha = new Polygon();

        if (derecha) {
            flecha.getPoints().addAll(
                    tamaño * 0.1, tamaño * 0.3,
                    tamaño * 0.7, tamaño * 0.3,
                    tamaño * 0.7, tamaño * 0.1,
                    tamaño * 0.9, tamaño * 0.5,
                    tamaño * 0.7, tamaño * 0.9,
                    tamaño * 0.7, tamaño * 0.7,
                    tamaño * 0.1, tamaño * 0.7
            );
        } else {
            flecha.getPoints().addAll(
                    tamaño * 0.3, tamaño * 0.1,
                    tamaño * 0.3, tamaño * 0.9,
                    tamaño * 0.1, tamaño * 0.7,
                    tamaño * 0.1, tamaño * 0.3
            );
        }

        flecha.setFill(Color.rgb(255, 215, 0, 0.7)); // Oro semitransparente
        flecha.setStroke(Color.rgb(218, 165, 32)); // Oro oscuro
        flecha.setStrokeWidth(2);
        flecha.setEffect(crearBrillo());

        return flecha;
    }

    /**
     * Crea una celda de tablero con efecto de agua.
     */
    public static Group crearCeldaTablero(double tamaño) {
        Rectangle celda = new Rectangle(tamaño, tamaño);
        celda.setFill(COLOR_TABLERO);
        celda.setStroke(COLOR_BORDE);
        celda.setStrokeWidth(1.5);
        celda.setArcWidth(5);
        celda.setArcHeight(5);

        // Efecto de olas (líneas onduladas)
        Path ola = new Path();
        ola.getElements().addAll(
                new MoveTo(tamaño * 0.1, tamaño * 0.5),
                new QuadCurveTo(tamaño * 0.3, tamaño * 0.4, tamaño * 0.5, tamaño * 0.5),
                new QuadCurveTo(tamaño * 0.7, tamaño * 0.6, tamaño * 0.9, tamaño * 0.5)
        );
        ola.setStroke(Color.rgb(173, 216, 230, 0.5)); // Azul claro
        ola.setStrokeWidth(1);
        ola.setStrokeLineCap(StrokeLineCap.ROUND);

        return new Group(celda, ola);
    }

    // ========== MÉTODOS DE UTILIDAD ==========

    /**
     * Aplica efecto de hover a una figura.
     */
    public static void aplicarEfectoHover(Shape figura) {
        figura.setOnMouseEntered(e -> {
            figura.setScaleX(1.1);
            figura.setScaleY(1.1);
            figura.setEffect(crearBrillo());
        });

        figura.setOnMouseExited(e -> {
            figura.setScaleX(1.0);
            figura.setScaleY(1.0);
            figura.setEffect(crearSombraExterna());
        });
    }

    /**
     * Crea un grupo con todas las figuras para un tipo de barco.
     */
    public static Group crearFiguraBarcoPorTipo(String tipo, double ancho, double alto, boolean horizontal) {
        switch (tipo.toUpperCase()) {
            case "PORTAVIONES":
                return crearPortaaviones(ancho, alto);
            case "DESTRUCTOR":
                return crearDestructor(ancho, alto);
            case "FRAGATA":
                return crearFragata(ancho, alto);
            case "SUBMARINO":
                return crearSubmarino(ancho, alto);
            default:
                // Usar el método genérico con el tipo específico
                return crearBarcoFigura(ancho, alto, horizontal, tipo);
        }
    }

    /**
     * Crea la figura correspondiente a un resultado de disparo.
     */
    public static Group crearFiguraResultado(String resultado, double tamaño) {
        switch (resultado.toUpperCase()) {
            case "TOCADO":
                return crearCirculoTocado(tamaño);
            case "HUNDIDO":
                return crearCirculoHundido(tamaño);
            case "REPETIDO":
                return crearMarcaRepetido(tamaño);
            default:
                return crearCirculoAgua(tamaño);
        }
    }

    /**
     * Obtiene el color principal de un tipo de barco.
     */
    public static Color getColorPorTipoBarco(String tipoBarco) {
        return switch (tipoBarco.toUpperCase()) {
            case "DESTRUCTOR" -> COLOR_DESTRUCTOR;
            case "FRAGATA" -> COLOR_FRAGATA;
            case "SUBMARINO" -> COLOR_SUBMARINO;
            default -> COLOR_PORTAVIONES;
        };
    }
}