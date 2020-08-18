package com.agp.demo.codedesign;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

/**
 * 观察者模式，里面有责任链模式的类似。添加观察者。
 *observer
 * hook
 * listener
 * callback
 * 皆是观察者模式
 */
public class ObserverAnnotation {
    public static void main(String[] args) {
        Button button=new Button();
        button.addListener(new LaoLiListener())
                .addListener(new LaoLiListener());
        button.buttonPressed();
    }


}

class Button{
    //类似于hook 将hook放入，等待触发。
    private List<ActionListener> observers=new ArrayList<>();

    public Button addListener(ActionListener listener){
        observers.add(listener);
        return this;
    }
    public void buttonPressed(){
        System.out.println("Button's press is done ...");
        for (ActionListener actionListener:observers){
            actionListener.listenAction(new ButtonPressedEvent(this));
        }

    }
}

/**
 * 观察者们应该实现的接口
 */
interface ActionListener{
    void listenAction(ActionEvent event);
}

class LaoLiListener implements ActionListener{

    @Override
    public void listenAction(ActionEvent event) {
        System.out.println(event.getSource().getClass().getName()+" fired a event.");
        System.out.println("LaoLi got the msg at time: "+event.curTime);
    }
}


class ActionEvent extends EventObject{
    public long curTime=System.currentTimeMillis();

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ActionEvent(Object source) {
        super(source);
    }
}
class ButtonPressedEvent extends ActionEvent{

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ButtonPressedEvent(Object source) {
        super(source);
    }
}