#!/usr/bin/env groovy
package jira

import confluence.ConfluenceConfigBuilder

File file = new File('confluence.config')

if (file.exists()) {
    println "Executing ${file.getAbsolutePath()}"
    def binding = new Binding()
    def shell = new GroovyShell(binding)
    binding.setVariable('confluence',{ c -> return ConfluenceConfigBuilder.build(c)})
    shell.evaluate(new File('confluence.config'))
    println "Executed ${file.getAbsolutePath()}"
} else {
    println "${file.getAbsolutePath()} doesn't exist"
}

