/*
 * Copyright 2016 OPEN TONE Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

Date.prototype.format = function(fmt) {

  var dt = this;

  var zero = function(n, l){
    var t = ""+n;
    while( t.length<l ) t = "0"+t;
    return t.substr(t.length-l,l);
  };

  return fmt.replace(/Y+|M+|D+|w+|H+|h+|a+|m+|s+(\.s+)?/g, function(word) {

    var type = word.charAt(0);
    var len = word.length;
    var num = 0;
    var ret = "";

    switch( type )
    {
      case 'Y':
        num = dt.getFullYear ? dt.getFullYear() : dt.getYear() + 2000;

        if( type == 'Y' && len == 4 ) ret = zero(num, 4);
        else if( len == 2 ) ret = zero(num, 2);
        else ret = num;
        break;

      case 'M':
        num = dt.getMonth() + 1;
        if( len == 4 ) ret = (['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'])[num-1];
          else if( len == 3 ) ret = (['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'])[num-1];
          else if( len == 2 ) ret = zero(num, 2);
          else ret = num;
        break;

      case 'D':
        num = dt.getDate();
        if( len == 2 ) ret = zero(num, 2);
        else ret = num;
        break;

      case 'w':
        num = dt.getDay();
        if( type == 'w' && len == 4 ) ret = (["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"])[num];
        else if( type == 'w' && len == 3 ) ret = (["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"])[num];
        else if( len == 2 ) ret = zero(num, 2);
        else ret = num;
        break;

      case 'h':
      case 'H':
        num = dt.getHours();
        if( type=='h' ) num %= 12;
        if( len == 2 ) ret = zero(num, 2);
        else ret = num;
        break;

      case 'a':
        num = dt.getHours()>=12 ? 1 : 0;
        ret = (["AM","PM"])[num];
        break;

      case 'm':
        num = dt.getMinutes();
        if( len == 2 ) ret = zero(num, 2);
        else ret = num;
        break;

      case 's':
        num = dt.getSeconds();

        var len2 = 0, p;
        if( (p=word.indexOf("."))>=0 ) { len2 = len - p - 1; len = p; }

        if( len == 2 ) ret = zero(num, 2);
        else ret = num;

        if( len2>0 ) {
        var ms = zero(dt.getMilliseconds(), 3);
        while( len2>ms.length ) ms+="0";
        ret += "."+ms.substr(0,len2);
      }
    }
    return ret;
  });
};