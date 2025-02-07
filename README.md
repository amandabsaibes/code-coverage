# Code Coverage Lab

<!-- 
    Optional: Add a coverage badge to your README to show the coverage on your project.
    E.g.: [![codecov](https://codecov.io/gh/<github-username>/code-coverage/branch/main/graph/badge.svg?token=<codecov-token>)](https://codecov.io/gh/<github-username>/code-coverage)
-->

## Prerequisites

1. Install Java 11. Note that you will not need to install Maven because you can run Maven wrapper with `./mvnw` (or `./mvnw.cmd` on Windows).
    * _Optional_: Install Java 11 with a version manager tool like [SDKMAN!](https://sdkman.io/install)
    * _Alternative_: Use [jenv](https://www.jenv.be/) on MacOS with [Homebrew](https://brew.sh/)
      ```bash
      brew install java11
      sudo ln -sfn /usr/local/opt/openjdk@11/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-11.jdk
      jenv add /Library/Java/JavaVirtualMachines/openjdk-11.jdk/Contents/Home/
      jenv shell 11
      ```
2. Fork this repository and clone it
    ```bash
   git clone https://github.com/your-username/code-coverage
   ```
3. _Optional_: If you have SDKMAN! installed, you can run `sdk env` at the root of the project to configure the environment

## Running

To verify you have set up the application correctly, run the Spring Boot console application.
At the root of the project, run
```bash
./mvnw spring-boot:run
```

## Coverage

1. Run the unit tests for the project and verify that all tests pass
   ```bash
   ./mvnw clean test
   ```
   You should see the following output in the console
   ```text
   ...
   [INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
   [INFO]
   [INFO] ------------------------------------------------------------------------
   [INFO] BUILD SUCCESS
   [INFO] ------------------------------------------------------------------------
   ...
   ```
2. Add JaCoCo to your project. In the [`pom.xml`](./pom.xml) file, add the JaCoCo plugin and configure it to generate a coverage report during the test phase.
   Add a coverage check to ensure that the ratio for lines of code covered by unit tests is at least 85%.
   Refer to the [JaCoCo documentation](https://www.eclemma.org/jacoco/trunk/doc/check-mojo.html) for more information on coverage checks.
   <details>
      <summary>Solution <i>(Attempt to complete this step on your own first!)</i></summary>   
      
      ```xml
      <plugin>
          <groupId>org.jacoco</groupId>
          <artifactId>jacoco-maven-plugin</artifactId>
          <!-- Or, the latest JaCoCo Maven plugin version -->
          <version>0.8.6</version>
          <configuration>
              <!-- Exclude the main application class -->
              <excludes>
                  <exclude>**/CodecoverageApplication.*</exclude>
              </excludes>
          </configuration>
          <executions>
              <execution>
                  <goals>
                      <goal>prepare-agent</goal>
                  </goals>
              </execution>
              <execution>
                  <!--
                      Generates the JaCoCo coverage report during the Maven test phase.
                      JaCoCo report is generated after all unit tests complete successfully.
                  -->
                  <id>report</id>
                  <phase>test</phase>
                  <goals>
                      <goal>report</goal>
                  </goals>
              </execution>
              <execution>
                  <!-- Coverage configuration -->
                  <id>check-coverage</id>
                  <phase>test</phase>
                  <goals>
                      <goal>check</goal>
                  </goals>
                  <configuration>
                      <!--
                          See https://www.eclemma.org/jacoco/trunk/doc/check-mojo.html for additional information
                          on configuring jacoco:check
                      -->
                      <skip>false</skip>
                      <haltOnFailure>true</haltOnFailure>
                      <rules>
                          <rule>
                              <!--
                                  Checks for sufficient coverage across the entire bundle.
                                  Additional scopes include: PACKAGE, CLASS, SOURCEFILE, and METHOD.
                              -->
                              <element>BUNDLE</element>
                              <limits>
                                  <!--
                                      Check that lines of code covered by unit tests out of total lines in the
                                      application should be above 85%.
                                      Additional limits include: INSTRUCTION, BRANCH, COMPLEXITY, METHOD, and
                                      CLASS.
                                      Additional values for limits include: TOTALCOUNT, COVEREDCOUNT, and
                                      MISSEDCOUNT.
                                  -->
                                  <limit>
                                      <counter>LINE</counter>
                                      <value>COVEREDRATIO</value>
                                      <minimum>0.85</minimum>
                                  </limit>
                                  <!-- TODO: In step 8, add another check for branch coverage here -->
                              </limits>
                          </rule>
                      </rules>
                  </configuration>
              </execution>
          </executions>
      </plugin>
      ```
   </details>
3. Run the unit tests again
   ```bash
   ./mvnw clean test
   ```
   Notice that the `jacoco:check` goal has failed with the following console output
   ```bash
   ...
   [INFO] --- jacoco-maven-plugin:0.8.6:check (check-coverage) @ codecoverage ---
   [INFO] Loading execution data file /Users/caleb.fung/Documents/2021/sigs/devops/lab/code-coverage/target/jacoco.exec
   [INFO] Analyzed bundle 'codecoverage' with 2 classes
   [WARNING] Rule violated for bundle codecoverage: lines covered ratio is 0.59, but expected minimum is 0.85
   [INFO] ------------------------------------------------------------------------
   [INFO] BUILD FAILURE
   [INFO] ------------------------------------------------------------------------
   ...
   ```
   We'll update the test necessary to fix the build. First, let's take a look at the JaCoCo coverage report.
   JaCoCo generates `jacoco.exec` - a binary file with coverage report data.
   You can view the coverage report from the [index.html](./target/site/jacoco/index.html) file.
4. You can leverage coverage checks or metrics in code coverage frameworks like JaCoCo to fail your build in a CI pipeline.
   When you integrate coverage checks and report generation into your CI pipelines,
   you can automate the coverage check process and encourage continuous improvement in a DevOps environment.
   To run unit tests and generate a coverage report, update the [`build.yml`](./.github/workflows/build.yml) GitHub Actions workflow.
   <details>
      <summary>Solution <i>(Attempt to complete this step on your own first!)</i></summary>   
      
      ```yaml
      - name: Run tests
        run: mvn test
      ```
   </details>

   Typically, in a project with a CI pipeline, you would include a coverage step to ensure that code that is added/updated are covered by unit tests.
   If there is insufficient code coverage, the build should fail.
   Commit your changed files and push them up to the main branch
   ```bash
   git add .github/workflows/build.yml pom.xml
   git commit -m "ci: Run unit tests and generate coverage report in GitHub Actions workflow"
   git push
   ```
   Navigate to the Actions tab and select the Coverage workflow to see your workflow run.
5. Note that the Run tests job within the Coverage workflow will fail.
   From the console logs, confirm that the job failed because code coverage constraints haven't been met
   ```text
   ...
   [INFO] --- jacoco-maven-plugin:0.8.6:check (check-coverage) @ codecoverage ---
   [INFO] Loading execution data file /Users/caleb.fung/Documents/2021/sigs/devops/lab/code-coverage/target/jacoco.exec
   [INFO] Analyzed bundle 'codecoverage' with 2 classes
   WARNING: Rule violated for bundle codecoverage: lines covered ratio is 0.59, but expected minimum is 0.85
   [INFO] ------------------------------------------------------------------------
   [INFO] BUILD FAILURE
   [INFO] ------------------------------------------------------------------------
   ...
   ```
6. You can configure a code coverage tool like [Codecov](https://about.codecov.io/) for more features and insights on code coverage for you application.
   For this lab, we'll simply upload the [`jacoco.xml`](./target/site/jacoco/jacoco.xml) coverage report file to Codecov, which will generate an interactive starburst chart.
   Head over to the Codecov site and register your code coverage GitHub repo, e.g., `https://github.com/<github-username>/code-coverage`.
   Once, your repo has been configured, navigate to the Settings tab and copy the Repository Upload Token from the General section.
   Your token should look like the following
   ```text
   xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
   ```
   To add this token to your GitHub repo, navigate to your GitHub repo and select the Settings tab.
   Go to the Secrets section and click on New Repository Secret.
   Add the following values for your repository secret
   
   | Field  | Value                                    |
   | ------ | ---------------------------------------- |
   | Name   | CODECOV_TOKEN                            |
   | Value  | \<Your Codecov Repository Upload Token\> |
   
   _Optional_: Add a coverage badge to this README by navigating to the Badge section in the Settings tab of your Codecov project.
   Copy the text under Markdown and paste it at the top of this README file.
7. To fix the build and increase code coverage, update the unit test for [`UnscrableServiceTest#unscramblesMatrix`](./src/test/java/com/credera/codecoverage/service/UnscrambleServiceTest.java).
   <details>
      <summary>Solution <i>(Attempt to complete this step on your own first!)</i></summary>   
      
      ```java
      class UnscrambleServiceTest {
         // ...
         void unscramblesMatrix() {
             final int[][] matrix = {{3, 3}, {6, 6}};
             final String expectedLines = "==\n==";
      
             String unscrabledMatrix = unscrambleService.unscramble(matrix);
      
             assertEquals(unscrabledMatrix, expectedLines);
         }
      }
      ```
   </details>

   Commit and push up your changes to run the GitHub Actions workflow
   ```bash
   git add pom.xml
   git commit -m "test: Update unscramble service unit test to ensure it unnscrambles a matrix"
   git push
   ```
   Code coverage reports are a great way to show business and leadership how stable an application's code generally is.
8. Add an additional check for branch coverage in the `pom.xml` file.
   There won't be any noticeable change to the coverage results because code coverage is already at 100%.
   <details>
      <summary>Solution <i>(Attempt to complete this step on your own first!)</i></summary>
         
      ```xml
      <limit>
         <counter>BRANCH</counter>
         <value>COVEREDRATIO</value>
         <minimum>0.9</minimum>
      </limit>
      ```
   </details>
   
   Commit and push up your changes to run the GitHub Actions workflow
   ```bash
   git add pom.xml
   git commit -m "ci: Add branch coverage check"
   git push
   ```

## Future Considerations

* In addition to statement and branch coverage, there are other coverage metrics you should consider for code.
  [NASA's guide](https://shemesh.larc.nasa.gov/fm/papers/Hayhurst-2001-tm210876-MCDC.pdf) on Modified Condition/Decision Coverage includes a great table on the types of structural coverage
  
  | Coverage Criteria                                                                                  | Statement<br>Coverage | Decision<br>Coverage | Condition<br>Coverage | Condition/<br>Decision<br>Coverage | MC/DC | Multiple<br>Condition<br>Coverage |
  |----------------------------------------------------------------------------------                  | :-------------------: | :------------------: | :-------------------: | :--------------------------------: | :---: | :-------------------------------: |
  | Every point of entry and exit in the<br>program has been invoked at least<br>once                  |                       | *                    | *                     | *                                  | *     | *                                 |
  | Every statement in the program<br>has been invoked at least once                                   | *                     |                      |                       |                                    |       |                                   |
  | Every decision in the program has<br>taken all possible outcomes at least<br>once                  |                       | *                    |                       | *                                  | *     | *                                 |
  | Every condition in a decision in the<br>program has taken all possible<br>outcomes at least once   |                       |                      | *                     | *                                  | *     | *                                 |
  | Every condition in a decision has<br>been shown to independently affect<br>that decision’s outcome |                       |                      |                       |                                    | *     | *<sup>+</sup>                     |
  | Every combination of condition<br>outcomes within a decision has<br>been invoked at least once     |                       |                      |                       |                                    |       | *                                 |

  <sup>+</sup> _Multiple condition coverage does not explicitly require showing the independent effect of each condition.
  This will be done, in most cases, by showing that every combination of decision inputs has been invoked.
  Note, however, that logical expressions exist wherein every condition cannot have an independent effect._

* Besides GitHub actions, there are other CI solutions you can explore to integrate and automate code coverage reports with
   * Jenkins
   * GitLab CI
   * Azure DevOps
   * JetBrains TeamCity
   * AWS CodeBuild
* Implementing code coverage in a project and generating code coverage reports isn't enough to ensure high code quality.
   It's also important to integrate a static code analysis tool like [SonarQube](https://www.sonarqube.org/) to detect bugs, code smells, and security vulnerabilities.
   JaCoCo integrates nicely with SonarQube.
* In addition to implementing static code analysis tools, you can track metrics for your application with tools such as [Graphite](https://graphiteapp.org/) or [Prometheus](https://prometheus.io/).
   These metrics can help you and your client make informed decisions to improve user experience.
