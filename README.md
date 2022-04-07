# CSV
Fast, zero-dependency, declarative CSV file reader for Java.
## Usage
Documentation and GitHub repository are currently under development. A quick and dirty implementation example is as follows:

`App.java`
```java
...
public static class Entry {
  public Integer a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z;
}
public static void main(String[] args) throws IOException {  
  Path input = new File("integers.csv").toPath();
  CSV<Entry> csv = new CSV<>(Record::new);
  Entry[] results = csv.read(input);
}
...
```
`integers.csv`
```csv
a           ,b           ,c           ,d           ,e           ,f           ,g           ,h           ,i           ,j           ,k           ,l           ,m           ,n           ,o           ,p           ,q           ,r           ,s           ,t           ,u           ,v           ,w           ,x           ,y           ,z
757612172   ,1673182854  ,1595244839  ,-183598274  ,-1091347740 ,1503515412  ,            ,-139752583  ,-977753868  ,1943408568  ,-303925210  ,-1025415426 ,-570653406  ,390313186   ,            ,-1230375674 ,1257572239  ,            ,-1874131371 ,-1232503334 ,548021053   ,1542140936  ,-108723445  ,            ,-1934046042 ,
1786788532  ,-1080811899 ,-832667733  ,-572711392  ,-1005470517 ,-1052691986 ,609595066   ,-1005093799 ,2126810425  ,-1352580377 ,106147409   ,1892362145  ,-1609718687 ,            ,-308054926  ,485163856   ,            ,-316087483  ,733623537   ,-2132551671 ,124409806   ,1815964056  ,            ,-2029373841 ,            ,62271763
525622720   ,-1415125868 ,-1117757539 ,-1763872306 ,1293643618  ,1404575285  ,-692535770  ,941819225   ,-1498193329 ,1918845393  ,-1518770620 ,            ,176282469   ,-1474942414 ,            ,1204980421  ,645517677   ,117567453   ,1057003440  ,-2110578398 ,-421958411  ,749989321   ,533013364   ,-1989411581 ,-210473171  ,
...
```

The CSV reader will automatically set the values of public fields in the objects created by the method passed to the CSV constructor. Any type with a static `valueOf(String)` method can be parsed from the CSV file.
