package com.heychinaski.ld29;

import java.applet.Applet;
import java.awt.BorderLayout;

public class MainApplet extends Applet {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  Game29 game = null;

  @Override
  public void start() {
    super.start();
    resize(1024, 768);
    setLayout(new BorderLayout());
    game = new Game29();
    add(game, BorderLayout.CENTER);
    
    new Thread() {
      public void run() {
        game.start();
      }
    }.start();
    
    game.requestFocus();
  }
  
  @Override
  public void stop() {
    super.stop();
  }
}