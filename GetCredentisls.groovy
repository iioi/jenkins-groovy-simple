// this is a DSLD file
// start off creating a custom DSL Descriptor for your Groovy DSL

// The following snippet adds the 'newProp' to all types that are a subtype of GroovyObjects
// contribute(currentType(subType('groovy.lang.GroovyObject'))) {
//   property name : 'newProp', type : String, provider : 'Sample DSL', doc : 'This is a sample.  You should see this in content assist for GroovyObjects: <pre>newProp</pre>'
// }
import jenkins.model.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey

def showRow = {credentialType,secretId,username=null,password=null,description=null->
	println("->"+"${credentialType}".padLeft(20)+"|"+secretId?.padRight(38)+"|"+
		username?.padRight(20)+"|"+password?.padRight(40)+"|"+description);
}

//set Credentials domain name (null means global)
domainName = null

credentialsStore = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0]?.getStore();
domain = new Domain(domainName,null,Collections.<DomainSpecification>emptyList())

credentialsStore?.getCredentials(domain).each{
	if(it instanceof UsernamePasswordCredentialsImpl) {
		//print 'username/passwrod\n'
		showRow("user/password",it.id,it.username,it.password?.getPlainText(),it.description)
	}else if(it instanceof BasicSSHUserPrivateKey) {
		//print 'basic ssh\n'
		//no use for now 20170713
		showRow("ssh private key",it.id,it.passphrase?.getPlainText(),it.privateKeySource?.getPrivateKey(),it.description)
	}else
		showRow("something eles")
}

return