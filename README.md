# SQLUtil 

A Simple Query Builder for Java

[![Build Status](https://travis-ci.org/ulisse1996/SqlUtil.svg?branch=master)](https://travis-ci.org/ulisse1996/SqlUtil) [![codecov](https://codecov.io/gh/ulisse1996/SqlUtil/branch/master/graph/badge.svg)](https://codecov.io/gh/ulisse1996/SqlUtil) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=it.donatoleone%3Asql-util&metric=alert_status)](https://sonarcloud.io/dashboard?id=it.donatoleone%3Asql-util) [![Bugs](https://sonarcloud.io/api/project_badges/measure?project=it.donatoleone%3Asql-util&metric=bugs)](https://sonarcloud.io/dashboard?id=it.donatoleone%3Asql-util) [![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=it.donatoleone%3Asql-util&metric=code_smells)](https://sonarcloud.io/dashboard?id=it.donatoleone%3Asql-util) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=it.donatoleone%3Asql-util&metric=alert_status)](https://sonarcloud.io/dashboard?id=it.donatoleone%3Asql-util)


## Examples

### Select
```java

SqlUtil.select()
      .from("TABLE"); // SELECT * FROM TABLE 

SqlUtil.select("COL1", "COL2")
      .from("TABLE"); // SELECT COL1, COL2 FROM TABLE

SqlUtil.select(AliasFactory.as("COL1","COLUMN1"),AliasFactory.column("COL2"))
      .from("TABLE"); // SELECT COL1 AS COLUMN1, COL2 FROM TABLE
      
SqlUtil.select(AliasFactory.as("COL1","COLUMN1"),AliasFactory.column("COL2"))
      .from("TABLE")
      .where("COL3").isEqualsTo(3); // SELECT COL1 AS COLUMN1, COL2 FROM TABLE WHERE COL3 = 3
      
SqlUtil.select(AliasFactory.as("COL1","COLUMN1"),AliasFactory.column("COL2"))
      .from("TABLE")   
      .where("COL3").in(Arrays.asList(50,60)); // SELECT COL1 AS COLUMN1 , COL2 FROM TABLE WHERE COL3 IN (50,60)
      
      
SqlUtil.select("COL1", "COL2")
      .from("TABLE")
      .where(
        WhereFactory.compoundWhere(
                WhereFactory.where("COL3").isEqualsTo(3),
                WhereFactory.orWhere("COL4").isEqualsTo(4)
        )
      ); // SELECT COL2 , COL1 FROM TABLE WHERE (COL3 = 3 OR COL4 = 4)
      
SqlUtil.select()
      .from("TABLE")
      .join(JoinType.INNER_JOIN, "TABLE1")
      .on("COL1").isEqualsToColumn("COL4"); // SELECT * FROM TABLE JOIN TABLE1 ON COL1 = COL4
      
SqlUtil.select()
      .from("TABLE1")
      .join(JoinType.INNER_JOIN, "TABLE2")
      .on(
        OnFactory.compoundOn(
                OnFactory.on("COL3").isEqualsTo(3),
                OnFactory.orOn("COL3").isEqualsTo(4)
        )
      ); // SELECT * FROM TABLE1 JOIN TABLE2 ON (COL3 = 3 OR COL3 = 4)
```
