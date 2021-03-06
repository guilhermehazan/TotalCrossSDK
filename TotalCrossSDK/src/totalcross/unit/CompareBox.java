/*********************************************************************************
 *  TotalCross Software Development Kit                                          *
 *  Copyright (C) 2000-2012 SuperWaba Ltda.                                      *
 *  All Rights Reserved                                                          *
 *                                                                               *
 *  This library and virtual machine is distributed in the hope that it will     *
 *  be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.                         *
 *                                                                               *
 *  This file is covered by the GNU LESSER GENERAL PUBLIC LICENSE VERSION 3.0    *
 *  A copy of this license is located in file license.txt at the root of this    *
 *  SDK or can be downloaded here:                                               *
 *  http://www.gnu.org/licenses/lgpl-3.0.txt                                     *
 *                                                                               *
 *********************************************************************************/



package totalcross.unit;

import totalcross.ui.event.*;
import totalcross.ui.gfx.*;
import totalcross.ui.image.*;
import totalcross.sys.*;
import totalcross.ui.*;

/** This class is used to show two images, side by side, and show the first difference between them. */

public class CompareBox extends Window
{
   private Button btn;
   private TimerEvent t;
   private int i,j,w;
   private Graphics myg0,myg1;
   private ImageControl original,test;
   private Check blink;

   public CompareBox(String title, int[] rgbs, int me, int ot, int i, int j, int n, byte[] originalBytes, ImageTester testImg)
   {
      super(title+" failed", ROUND_BORDER);
      setBackColor(0xF0F0F0);
      Label msg = new Label(Convert.unsigned2hex(me,6)+" != "+Convert.unsigned2hex(ot,6)+" at "+i+","+j);
      int scale = (Settings.screenWidth-20)/testImg.getWidth()/2;
      Image imgo,imgi;
      try
      {
         imgo = reconstroyImage(testImg, rgbs, n, originalBytes, testImg.getWidth(), testImg.getHeight()).scaledBy(scale,scale);
         imgi = testImg.scaledBy(scale,scale);
      }
      catch (ImageException e)
      {
         return;
      }
      original = new ImageControl(imgo);
      test = new ImageControl(imgi);
      int w = original.getPreferredWidth() * 2 + 16;
      int h = msg.getPreferredHeight() * 5 + original.getPreferredHeight();
      setRect(CENTER,CENTER,w,h);
      add(new Label("Original"),LEFT,TOP);
      add(new Label("Test"),RIGHT,SAME);
      add(original, LEFT+3,AFTER+2);
      add(test, RIGHT-3, SAME);
      add(msg, CENTER,AFTER+3);
      add(btn = new Button("  Close  "),RIGHT,BOTTOM-2);
      add(blink = new Check("Blink"),LEFT,SAME);
      //blink.setChecked(true);
      this.i = i*scale;
      this.j = j*scale;
      this.w = scale;
   }

   private Image reconstroyImage(Image testImg, int[] rgbs, int n, byte[] originalBytes, int k, int l) throws ImageException
   {
      int w = testImg.getWidth();
      int h = testImg.getHeight();
      Image original = new Image(w,h);
      Graphics ig = original.getGraphics();
      byte[] otherbytes = originalBytes;
      int ofs = 0;
      for (int j = 0; j < h; j++)
      {
         for (int i =0; i < w; i++)
         {
            ig.foreColor = rgbs[otherbytes[ofs++] & 0xFF];
            ig.setPixel(i,j);
         }
         ofs += n-w;
      }
      return original;
   }

   protected void postPopup()
   {
      myg0 = original.getGraphics();
      myg1 = test.getGraphics();
      if (blink.isChecked()) t = addTimer(1000);
   }

   private boolean showing;

   private void doBlink()
   {
      myg0.drawRect(i,j,w,w);
      myg1.drawRect(i,j,w,w);
      showing = !showing;
   }

   public void onEvent(Event e)
   {
      switch (e.type)
      {
         case TimerEvent.TRIGGERED:
            if (t != null && t.triggered)
               doBlink();
            break;
         case ControlEvent.PRESSED:
            if (e.target == btn)
            {
               if (t != null) removeTimer(t);
               t = null;
               unpop();
            }
            else
            if (e.target == blink)
            {
               if (!blink.isChecked())
               {
                  if (t != null) removeTimer(t);
                  t = null;
                  if (showing) doBlink();
               }
               else
               {
                  t = addTimer(1000);
               }
            }
            break;
      }
   }
}
