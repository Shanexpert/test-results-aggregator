# test-results-aggregator
It's a jenkins plugin that collects via Jenkins API all junit/testNG results and reports them : 
* in a single html view, 
* email

It's advisable to be used as the last step in a CI/CD pipeline. Currently supports only 'free style project' but can be integrated in a groovy pipeline script just by using a build action , for example : 

    stage("Aggregate Report") {
		  build job: 'My_aggregate_results_job'
    }

Requires both global and job configuration. Check below : 

### Global Configuration
After installing test-results-aggregator plugin , navigate to Global Configuration. Scroll for Test Result Aggregator , see image : 
![Global Configuration](https://github.com/sdrss/test/blob/master/screenshots/Global_Configuration.png)

In this section you can define : 
* **Jenkins Base Url** : The HTTP address of the Jenkins installation, such as http://yourhost.yourdomain/jenkins/. This value is used to access Jenkins API.
* **Jenkins Account Username** : username of the account that will be used to access Jenkins API and fetch job results.
* **Jenkins Account password** : password of the account that will be used to access Jenkins API and fetch job results.
* **Mail Notification From** : sender for the mail Notification. Default is "Jenkins".

### Job Configuration

**1**. Test Result Aggregator Plugin can be used as a "Free Style Project". Create a new by : 
  ![Free Style Project](https://github.com/sdrss/test/blob/master/screenshots/FreeStyleProject.png)

**2**. Select "Add Post Build" action and scroll to "Aggregate Test Results" action.
  ![Post Build Action](https://github.com/sdrss/test/blob/master/screenshots/PostBuildAction.png)

**3**. Add Groups/Teams and Jenkins Jobs : 
  ![Jobs Configuraion](https://github.com/sdrss/test/blob/master/screenshots/FreeStyleProject_Jobs.png)
* **Group/Team** : it's optional, it's used in report to group Jenkins jobs. For example teams , products or testing types.
* **Job Name** : It's mandatory, it's the exact Jenkins job name to get results.
* **Job Friendly Name** : it's optional, used only for reporting purposes, if null or empty then "Job Name" will be used in report.

**4**. Add Recipients List , Before,After Body text, theme and Sort by option : 
  ![Recipients](https://github.com/sdrss/test/blob/master/screenshots/ReceipientsList.png)
* **Recipients List** : comma separated recipients list , ex : nick@some.com,mairy@some.com .if empty or blank no email will be triggered.
* **Subject prefix** : prefix for mail subject.
* **Columns** : html & email report columns and the order of them, comma separated.
* **Before body** : plain text or html code to add before report table.
* **After body** : plain text or html code to add after report table.
* **Mail Theme** : Ligth or dark mail theme.
* **Sort Results By** : report will be sorted accordingly. If there are Groups then sorting refers to jobs inside a group.

**5**. Outdated results : 
   ![OutofDate](https://github.com/sdrss/test/blob/master/screenshots/OutofDate.png)
* **Out Of Date Results in Hours** : jobs with results more than X hours ago will be marked with 'red' color under 'Last Run' column report.
Otherwise (if blank or empty) then column 'Last Run' will just have the timestamp of job completion.

### Reports

1. Jobs and Tests graphs, see a sample :
  ![Main View](https://github.com/sdrss/test/blob/master/screenshots/MainView.png)

2. HTML Report , sample :
  ![html](https://github.com/sdrss/test/blob/master/screenshots/htmlView.png)
    * the html report is generated under workspace/html/index.html and can be published also via HTML Publisher Plugin
    * the same report is send via mail.
    
3. Aggregated view , sample : 
  ![Aggregated](https://github.com/sdrss/test/blob/master/screenshots/AggregatedView.png)
