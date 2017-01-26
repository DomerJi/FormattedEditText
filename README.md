# Formatted EditText
[![](https://jitpack.io/v/zTrap/FormattedEditText.svg)](https://jitpack.io/#zTrap/FormattedEditText)
-
<img src="/art/Screenshot_1.jpg" width="300">
-
## How to get FormattedEditText library 
#### With gradle:
-
Step 1. Add the **JitPack repository** to your **build.gradle** file.
```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
Step 2. Add the **dependency**
```gradle
dependencies {
   compile 'com.github.zTrap:FormattedEditText:0.0.1-beta2'
}
```

#### With maven:
-
Step 1. Add the **JitPack repository** to your **build file**.
```maven
<repositories>
	<repository>
	  <id>jitpack.io</id>
	  <url>https://jitpack.io</url>
	</repository>
</repositories>
```  
Step 2. Add the **dependency**
```maven
<dependency>
  <groupId>com.github.zTrap</groupId>
  <artifactId>FormattedEditText</artifactId>
  <version>0.0.1-beta1</version>
</dependency>
```
## Usage
**xml**
```xml
<ru.ztrap.views.FormattedEditText
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:z_format="+7 ({3}) {3}-{2}-{2}" //absolutely free template (in single-line)*
    app:z_textColor="@android:color/black"
    app:z_textSize="18sp"/>
```
*how to use this format see in Javadocs [FormattedEditText](https://jitpack.io/com/github/zTrap/FormattedEditText/0.0.1-beta1/javadoc/ru/ztrap/views/FormattedEditText.html).[setFormat()](https://jitpack.io/com/github/zTrap/FormattedEditText/0.0.1-beta1/javadoc/ru/ztrap/views/FormattedEditText.html#setFormat-java.lang.String-)

**java**
```java
FormattedEditText fet = (FormattedEditText) findViewById(R.id.fet);
fet.setWatcher(new FormattedEditText.zWatcher() {
          @Override
          public void beforeTextChanged(int id, CharSequence s, int start, int count, int after) {}
          @Override
          public void onTextChanged(int id, CharSequence s, int start, int before, int count) {}
          @Override
          public void afterTextChanged(int id, Editable s) {}
          @Override
          public void onAllCompleted(String onlyEntered, String withFormat) {}
      });
```

#License

    Copyright 2017 zTrap

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    
