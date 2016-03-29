# PatternLockView
a simple android pattern lock view widget.

[![Join the chat at https://gitter.im/geftimov/android-patternview](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/geftimov/android-patternview?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-android--patternview-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1495) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.eftimoff/android-patternview/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.eftimoff/android-patternview)

## Features
* It is simple and easy to use
* support n * n Pattern
* support auto link between two nodes
* support show the direction of the path
* support hidden or show the path
* Support `API LEVEL >= 8`

View for locking and unlocking.

![svg](https://github.com/geftimov/android-patternview/blob/master/art/rsz_empty_pattern.png) ![svg](https://github.com/geftimov/android-patternview/blob/master/art/rsz_pattern_correct.png) ![svg](https://github.com/geftimov/android-patternview/blob/master/art/rsz_mm.png) ![svg](https://github.com/geftimov/android-patternview/blob/master/art/rsz_small.png) ![svg](https://github.com/geftimov/android-patternview/blob/master/art/rsz_skyscrapers.png)

##### How to use

    <com.cc.library.PatternView
        xmlns:patternview="http://schemas.android.com/apk/res-auto"
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
|   cellRadius   	|     0     	|         The radius of the dot cell,If have set the cell radius the cell spacing will be ignored.            	|
|   cellBaseCount 	|     3      	|         the base count of the cell          	|
|   cellColor           |   #FFFFFF 	|          Color of the cell dot.                     	|
|   cellSpacing 	|     3     	|         Rows of the grid. Example 4 for 4xcolums.         	|
|   gridColumns  	|     3     	|         Columns of the grid. Example 4 for rowsx4.         	|
|  pathColor  	| #FFFFFF       | The color of the path that is following the pointer. 	|

##### Limitations

1. Padding for the view does not work.

#### TODO

1. See the padding , and why it is not applied.
2. Make wiki for all the settings.

#### Contributors

I want to update this library and make it better. So any help will be appreciated.
Make and pull - request and we can discuss it.

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
