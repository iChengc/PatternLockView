# PatternLockView
a simple android pattern lock view widget.

## Features
* It is simple and easy to use
* support n * n Pattern
* support auto link between two nodes
* support showing the direction of the path
* support hidden or showing the path
* Support `API LEVEL >= 8`

View for locking and unlocking.

![svg](https://github.com/iChengc/PatternLockView/blob/master/art/empty.jpg) ![svg](https://github.com/iChengc/PatternLockView/blob/master/art/setup.jpg) ![svg](https://github.com/iChengc/PatternLockView/blob/master/art/error.jpg) ![svg](https://github.com/iChengc/PatternLockView/blob/master/art/nopath.jpg) ![svg](https://github.com/iChengc/PatternLockView/blob/master/art/nXn.jpg)

##### How to use

    <com.cc.library.PatternView
        xmlns:custom="http://schemas.android.com/apk/res-auto"
        android:id="@+id/patternView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:padding="20dp"
        custom:cellRadius="@dimen/pattern_cell_radius"
        custom:cellBaseCount="3"
        custom:cellColor="@color/white"/>
        
##### Attributes

|     attr    	        |  default  	|                         mean                         	|
|:--------------------:	|:------------:	|:----------------------------------------------------:	|
|   cellRadius   	|     0     	|         The radius of the dot cell.                   	|
|   cellBaseCount 	|     3      	|         the base count of the cell          	            |
|   cellColor       |   #FFFFFF 	|         Color of the cell dot.                     	    |
|   cellSpacing 	|     32dp     	|         the spacing between two dot cells.                |
|   showPath     	|     true     	|         whether is showing path or not when unlocking.    |

##### Exception

It will throw no enough space exception, if the value of cell radius or spacing is too large.

## Handle result
* **Handle pattern password result:**
handle pattern password result when user complete draw the pattern
```java
mPatternView.setOnFinishListener(new PatternView.OnFinishListener() {
            @Override
            public boolean onFinish(PatternView patternView, List<Integer> result, String resultAsString) {

                    if (resultAsString.equals(mPassword)) {
                        Toast.makeText(MainActivity.this, "Password is correct.", Toast.LENGTH_LONG).show();
                        return true;
                    }
                    Toast.makeText(MainActivity.this, "Try again", Toast.LENGTH_LONG).show();
                    return false;
            }
        });
```

## Licence

    Copyright 2015 iChengc

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
