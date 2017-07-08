#!/usr/bin/env groovy
package jira

import bitbucket.BitbucketConfigBuilder

File file = new File('bitbucket.config')

if (file.exists()) {
    println "Executing ${file.getAbsolutePath()}"
    def binding = new Binding()
    def shell = new GroovyShell(binding)
    binding.setVariable('bitbucket',{ c -> return BitbucketConfigBuilder.build(c)})
    shell.evaluate(new File('bitbucket.config'))
    println "Executed ${file.getAbsolutePath()}"
} else {
    println "${file.getAbsolutePath()} doesn't exist"
}

