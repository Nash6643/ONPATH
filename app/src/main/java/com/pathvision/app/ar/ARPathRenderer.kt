package com.pathvision.app.ar

#ARPathRenderer

object ARPathRenderer {
    const val VERTEX_SHADER = """
        uniform mat4 u_ModelViewProjection;
        attribute vec4 a_Position;
        void main() {
            gl_Position = u_ModelViewProjection * a_Position;
        }
    """

    const val FRAGMENT_SHADER = """
        precision mediump float;
        uniform vec4 u_Color;
        void main() {
            gl_FragColor = u_Color;
        }
    """
}