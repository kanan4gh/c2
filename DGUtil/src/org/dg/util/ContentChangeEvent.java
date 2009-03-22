/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dg.util;

/**
 *
 * @author akira
 */
public class ContentChangeEvent {

    private Object origin;
    private Object value;

    public ContentChangeEvent(Object origin, Object value) {
        this.origin = origin;
        this.value = value;
    }

    public Object getOrigin() {
        return origin;
    }

    public Object getValue() {
        return value;
    }

}
