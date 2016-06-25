/* Author:      Craig Dearden
 * Date:        Jun 24, 2016
 * Name:        Action.java
 * Description: 
 */
package com.craigdearden.app;

public class Change {

    public enum Action {
        ADD, REMOVE
    }

    private Action _action = null;
    private String _data = null;
    private int _position = -1;

    Change() {}

    Change(Action action, String data, int position) {
        _action = action;
        _data = data;
        _position = position;
    }

    /**
     * @return the _action
     */
    public Action getAction() {
        return _action;
    }
    
    /**
     * @return the _data
     */
    public String getData() {
        return _data;
    }

    /**
     * @return the _position
     */
    public int getPosition() {
        return _position;
    }

}
