// vars/pythonUtils.groovy
def runPythonTests(String workspace) {
    dir(workspace) {
        sh """
        python3 -m venv venv
        . venv/bin/activate
        pip install -r requirements.txt
        coverage run -m pytest
        coverage report -m > coverage.xml
        """
    }
}
