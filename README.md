# tecsinapse-data-sync
Sync data between services/modules/systems must be easy

## Example (Draft)

In a Portal we create and manage user and credentials. In a CRM we receive these informations to know more details about our salespersons.

System 1 (Portal)
```
public class User {
    private Long id;
    private String name; 
    private String email;
    private String phone;
    private int age;
}
```

System 2 (CRM)
```
public class SalesPerson {
    private Long id;
    private String name; 
    private String email;
    private String phone;
}
```

How synchronize data between these two systems?

Ideas
- Using Hibernate listener with Envers

-- Pros: 

--- easy to implement in Java

-- Cons: 

--- changes directly in the database won't be detected, for instance, migration scripts.

--- only works between Java projects using Hibernate


## Solution 1

System 1 (Portal)
```
@TecDataSync
public class User {
    @TecDataSync(systems={CRM})
    private Long id;
    @TecDataSync(systems={CRM})
    private String name;
    @TecDataSync(systems={CRM})
    private String email;
    @TecDataSync(systems={CRM})
    private String phone;
    private int age;
}
```

System 2 (CRM)
```
public class SalesPerson {
    private Long id;
    private String name; 
    private String email;
    private String phone;
}
```
