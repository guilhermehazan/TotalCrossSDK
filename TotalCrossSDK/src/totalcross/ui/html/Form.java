/*********************************************************************************
 *  TotalCross Software Development Kit                                          *
 *  Copyright (C) 2003-2004 Pierre G. Richard                                    *
 *  Copyright (C) 2003-2012 SuperWaba Ltda.                                      *
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



package totalcross.ui.html;
import totalcross.net.*;
import totalcross.sys.*;
import totalcross.ui.*;
import totalcross.ui.event.*;
import totalcross.util.*;
import totalcross.xml.*;

/**
* <code>Form</code> links all Input element attached to it.
*
* @author  Jim Guistwite / Pierre G. Richard
*/
public class Form
{
   Form previous;
   public String method;
   public String url;
   Vector inputs;

   /**
   * Constructor
   *
   * @param previous previous form, if any. null otherwise.
   * @param atts tag attributes
   */
   public Form(Form previous, AttributeList atts)
   {
      this.previous = previous;
      inputs = new Vector(4);
      method = atts.getAttributeValue("method");
      url = atts.getAttributeValue("action");
   }

   /** Builds the URL with all form values. */
   public String buildURL(String buttonName)
   {
      int n = inputs.size();
      Hashtable ht = new Hashtable(n);
      for (int i=0; i < n; i++)
      {
         Control item = (Control)inputs.items[i];
         ControlProperties cl = (ControlProperties)item.appObj;
         String name = cl.name,old;
         if (name != null)
         {
            Vector listeners = item.getEventListeners();
            String val = getValue(item);
            if (val != null && listeners != null && ((Listener)listeners.items[0]).listener instanceof Document.SubmitReset && !val.equals(buttonName)) // guich@tc114_26: if its a submit button, add only the one that was pressed
               continue;
            if (!ht.exists(name))
               ht.put(name, val == null ? "" : URI.encode(val));
            else
            {
               old = (String)ht.get(name);
               ht.put(name, val == null ? old : (old.length() > 0 ? old+"=" : "")+URI.encode(val));
            }
         }
      }
      StringBuffer ret = new StringBuffer(url);
      if (ht.size() > 0)
      {
         ret.append("?");
         ht.dumpKeysValues(ret, "=","&");
      }
      return ret.toString();
   }
   
   /**
   * This method is called by the submit button of the form
   * to submit the contents of the form to the server.
   * It loops in all <b>named</b> containers of the form and get their value,
   * @param buttonValue The text of the button 
   */
   public void submit(String buttonValue)
   {
      HtmlContainer htmlCnr = getHtmlContainer();
      if (htmlCnr != null)
         htmlCnr.postLinkEvent(buildURL(buttonValue));
      else
         Vm.warning("Form can't be submitted because there are no inputs whose parent is a HtmlContainer.");
   }

   private HtmlContainer getHtmlContainer()
   {
      for (int i = inputs.size(); --i >= 0;)
         for (Control control = (Control)inputs.items[i]; control != null; control = control.getParent())
            if (control instanceof HtmlContainer)
               return (HtmlContainer)control;
      return null;
   }

   /**
   * This method is called by the reset button of the form
   * to reset the contents of the form to their default values.
   */
   public void reset()
   {
      int n = inputs.size();
      for (int i=0; i < n; ++i)
         ((Control)inputs.items[i]).clear();
   }

   /** Returns the value of the given Control. Used by this Form to build the url with parameters. */
   public static String getValue(Control c)
   {
      ControlProperties cp = (ControlProperties) c.appObj;
      if (c instanceof Edit)
         return ((Edit)c).getText();
      if (c instanceof Check)
         return ((Check)c).isChecked() ? (cp.value == null ? "on" : cp.value) : null;
      if (c instanceof Radio)
         return ((Radio)c).isChecked() ? cp.value : null;
      if (c instanceof MultiEdit)
         return ((MultiEdit)c).getText();
      if (c instanceof ComboBox)
      {
         ComboBox cb = (ComboBox)c;
         int sel = cb.getSelectedIndex();
         return (sel >= 0 || (sel = cb.clearValueInt) >= 0) ? ((Document.Entry) cb.getItemAt(sel)).key : null;
      }
      if (c instanceof MultiListBox)
      {
         MultiListBox mlb = (MultiListBox)c;
         StringBuffer sb = new StringBuffer(32);
         int n = mlb.size();
         if (n > 0)
            for (int i = 0,k=0; i < n; i++)
               if (mlb.isSelected(i))
                  sb.append(k++>0?",":"").append(((Document.Entry) mlb.getItemAt(i)).key);
         return sb.toString();
      }
      if (c instanceof ListBox) // must be placed after MultiListBox
         return ((ListBox)c).getSelectedIndex() >= 0 ? ((Document.Entry) ((ListBox)c).getSelectedItem()).key : null;
      return cp.value;
   }
   
   /** Sets the value of the given Control. */
   public static void setValue(Control c, String val)
   {
      ControlProperties cp = (ControlProperties) c.appObj;
      if (c instanceof Edit)
         ((Edit)c).setText(val);
      else
      if (c instanceof Check)
         ((Check)c).setChecked(val.equals(cp.value));
      else
      if (c instanceof Radio)
         ((Radio)c).setChecked(val.equals(cp.value));
      else
      if (c instanceof MultiEdit)
         ((MultiEdit)c).setText(val);
      else
      if (c instanceof ComboBox)
      {
         ComboBox cb = (ComboBox)c;
         cb.setSelectedItem(val);
      }
      else
      if (c instanceof MultiListBox)
      {
         MultiListBox mlb = (MultiListBox)c;
         String[] sels = Convert.tokenizeString(val,',');
         for (int i =0; i < sels.length; i++)
         {
            int j = mlb.indexOf(sels[i]);
            if (j != -1)
               mlb.setSelectedIndex(j,true);
         }
      }
      else
      if (c instanceof ListBox) // must be placed after MultiListBox
         ((ListBox)c).setSelectedItem(val);
   }
}
