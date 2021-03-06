/*********************************************************************************
 *  TotalCross Software Development Kit                                          *
 *  Copyright (C) 2003 Gilbert Fridgen                                           *
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



package totalcross.pim;

import totalcross.util.*;

public class RecordList
{
   Vector records;
   int position;

   public RecordList(Vector records)
   {
      this.records = records;
   }

   public boolean hasMoreRecords()
   {
      int n = records.size();
      for (;position < n; position++)
      {
         //find next not-null record
         if (records.items[position] != null)
            return true; // record found
      }
      return false; // no more records found, end of vector
   }

   public VersitRecord nextRecord()
   {
      VersitRecord vr = (VersitRecord)records.items[position];
      position++;
      return vr;
   }
}
