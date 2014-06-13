/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.aesh.cl.converter;

import org.jboss.aesh.cl.validator.OptionValidatorException;
import org.jboss.aesh.console.command.converter.ConverterInvocation;
import org.jboss.aesh.io.Resource;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class FileResourceConverter implements Converter<Resource, ConverterInvocation> {

    @Override
    public Resource convert(ConverterInvocation converterInvocation) throws OptionValidatorException {
        return converterInvocation.getAeshContext().getCurrentWorkingDirectory().newInstance(
                converterInvocation.getInput());
    }
}
