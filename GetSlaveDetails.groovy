import jenkins.model.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.plugins.credentials.domains.*
import groovy.json.JsonOutput

def slaveList = jenkins.model.Jenkins.instance.slaves.findAll()

def credentialsMap = [:]

credentialsStore = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0]?.getStore();
domain = new Domain(null,null,Collections.<DomainSpecification>emptyList())
credentialsStore?.getCredentials(domain).each{
  if(it instanceof UsernamePasswordCredentialsImpl){
    def map = [:]
    map.put('user',it.username)
    map.put('password',it.password?.getPlainText())
    credentialsMap.put(it.id,map)
  }
}

def reslist = []

slaveList.each{slave->
  def infoMap = [:]
  infoMap.put('host',slave.launcher.host)
  infoMap.put('numExecutors',slave.numExecutors)
  infoMap.put('label',slave.labelString)
  infoMap.put('availablePhysicalMemory',slave.computer.monitorData.'hudson.node_monitors.SwapSpaceMonitor'.'availablePhysicalMemory')
  infoMap.put('availableSwapSpace',slave.computer.monitorData.'hudson.node_monitors.SwapSpaceMonitor'.'availableSwapSpace')
  infoMap.put('totalPhysicalMemory',slave.computer.monitorData.'hudson.node_monitors.SwapSpaceMonitor'.'totalPhysicalMemory')
  infoMap.put('totalSwapSpace',slave.computer.monitorData.'hudson.node_monitors.SwapSpaceMonitor'.'totalSwapSpace')
  infoMap.put('ArchitectureMonitor',slave.computer.monitorData.'hudson.node_monitors.ArchitectureMonitor')
  def credentialsId = slave.launcher.credentialsId
  infoMap.put('credentialsId',credentialsId)
  infoMap.put('user',credentialsMap.get(credentialsId).user)
  infoMap.put('password',credentialsMap.get(credentialsId).password)
  reslist.add(infoMap)
}


return JsonOutput.toJson(reslist)