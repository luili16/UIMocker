# UIMocker
封装了对Android的UI模拟操作的库

## 如何使用

```
Solo solo = new Solo(getApplicationContext());
// click
solo.getClicker().clickOnText("...")
// scroll
solo.getScroller().scrollViewVertically(...)

...
```

