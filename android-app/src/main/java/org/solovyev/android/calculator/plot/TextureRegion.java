/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator.plot;

class TextureRegion {

    //--Members--//
    public float u1, v1;                            // Top/Left U,V Coordinates
    public float u2, v2;                            // Bottom/Right U,V Coordinates

    //--Constructor--//
    // D: calculate U,V coordinates from specified texture coordinates
    // A: texWidth, texHeight - the width and height of the texture the region is for
    //    x, y - the top/left (x,y) of the region on the texture (in pixels)
    //    width, height - the width and height of the region on the texture (in pixels)
    public TextureRegion(float texWidth, float texHeight, float x, float y, float width, float height) {
        this.u1 = x / texWidth;                        // Calculate U1
        this.v1 = y / texHeight;                        // Calculate V1
        this.u2 = this.u1 + (width / texWidth);    // Calculate U2
        this.v2 = this.v1 + (height / texHeight);    // Calculate V2
    }
}
