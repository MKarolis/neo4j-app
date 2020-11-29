<h1>Simple Neo4j App</h1>
<h6>
Created by Karolis Medekša for non-relational databases course<br/>
Vilnius University, Faculty of Mathematics and Informatics, 2020 Fall
</h6>

<h2>About</h2>
Simple console app built with `Spring Boot`.

The application uses `Neo4j` graph database for operation.

Application showcases simple examples of shortest path search in a graph, 
querying by deep relations, aggregating data and more.
<h2>Setup</h2>

- Pull git repo
- Execute query found in `SEED.txt` against your database
- Open with IDE of your choice
- Set the following variables to match with the instance of `Neo4j` you're using:
    - `spring.neo4j.uri`
    - `spring.neo4j.authentication.username`
    - `spring.neo4j.authentication.password`
- Run the application
