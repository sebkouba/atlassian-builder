#!/usr/bin/env groovy
import jira.JIRAConfigBuilder

File file = new File('jira.config')

if (file.exists()) {
    println "Executing ${file.getAbsolutePath()}"
    def binding = new Binding()
    def shell = new GroovyShell(binding)
    binding.setVariable('jira',{ c -> return JIRAConfigBuilder.build(c)})
    shell.evaluate(new File('jira.config'))
    println "Executed ${file.getAbsolutePath()}"
} else {
    println "${file.getAbsolutePath()} doesn't exist"
}

