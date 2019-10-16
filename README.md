# test-results-aggregator
It's a jenkins plugin that collects via api all Jenkins Jobs results and reports them : 
* in a single html view 
* email

Requires both global and job configuration. Check below 

### Global Configuration
After installing test-results-aggregator plugin , navigate to Global Configuration. Scroll and search for Test Result Aggregator , see image : 

![Global Configuration](https://github.com/sdrss/test/blob/master/screenshots/Global_Configuration.png)

In this section you can define : 
* Jenkins Base Url : The HTTP address of the Jenkins installation, such as http://yourhost.yourdomain/jenkins/. This value is used to access Jenkins API.
* Jenkins Account Username : the username of the account that will be used to access Jenkins API and fetch job results.
* Jenkins Account password : the password of the account that will be used to access Jenkins API and fetch job results.
* Mail Notification From : the e-mail address of the sender for the mail Notification. Default is Jenkins.

### Job Configuration

**1**. Test Result Aggregator Plugin can be used as a Free Style Project. Create a new Free style project by : 
  ![Free Style Project](https://github.com/sdrss/test/blob/master/screenshots/FreeStyleProject.png)

**2**. Select Add Post Build Action and scroll to Aggregate Test Results action.
  ![Post Build Action](https://github.com/sdrss/test/blob/master/screenshots/PostBuildAction.png)

**3**. Add Groups/Teams and Jenkins Jobs : 
  ![Jobs Configuraion](https://github.com/sdrss/test/blob/master/screenshots/FreeStyleProject_Jobs.png)
* Group/Team : Group or Team Name it's optional and used in report to group Jenkins jobs per team or test types.
* Job Name : Jenkins Job name it's mandatory , using this name this plugin will fetch results.
* Job Friendly Name : Jenkins 'Job Friendly Name' it's optional and used only for reporting purposes, if null or empty then Jenkins Job name will be used in report.

**4**. Add Recipients List , Before,After Body text, theme and Sort by option : 
  ![Recipients](https://github.com/sdrss/test/blob/master/screenshots/ReceipientsList.png)
* Recipients List : Comma separated recipients list , ex : nick@some.com,mairy@some.com .if empty or blank no email will be triggered.
* Before body : Text to add before report table. Plain text or html code.
* After body : Text to add after report table. Plain text or html code.
* Mail Theme : Ligth or dark theme.
* Sort Results By : report will sort results accordingly.

**5**. Outdated results : 
   ![OutofDate](https://github.com/sdrss/test/blob/master/screenshots/OutofDate.png)
* Of Date Results in Hours : Completed Jenkins Jobs with results more than X hours ago will be marked with 'red' color under 'Last Run' column report.
Otherwise if blank or empty then column 'Last Run' will just have the timestamp of job completion.

### Reports

1. Jobs and Tests graphs, see a sample :
  ![Main View](https://github.com/sdrss/test/blob/master/screenshots/MainView.png)

2. HTML Report , sample :
  ![html](https://github.com/sdrss/test/blob/master/screenshots/htmlView.png)
    * the html report is generated under workspace/html/index.html and can be published also via HTML Publisher Plugin
    * the same report is send via mail.
    
3. Aggregated view , sample : 
  ![Aggregated](https://github.com/sdrss/test/blob/master/screenshots/AggregatedView.png)
