package ro.kuberam.maven.plugins.expath.mojos;

import java.io.File;
import java.lang.reflect.Field;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.util.ReflectionUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;

public class KuberamAbstractMojoTestBase extends PlexusTestCase {

	protected static String baseDir = PlexusTestCase.getBasedir();
	protected static String projectBuildDirectory = baseDir + File.separator + "target";

	@Requirement
	protected RepositorySystem repositorySystem;

	@Requirement
	protected org.apache.maven.project.ProjectBuilder projectBuilder;

	@Override
	protected void setUp() throws Exception {
		repositorySystem = lookup(RepositorySystem.class);
		projectBuilder = lookup(org.apache.maven.project.ProjectBuilder.class);
	}

	@Override
	protected void tearDown() throws Exception {
		repositorySystem = null;
		projectBuilder = null;
		super.tearDown();
	}

	protected static RepositorySystem newRepositorySystem() {
		final DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
		locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
		locator.addService(TransporterFactory.class, FileTransporterFactory.class);
		locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

		return locator.getService(RepositorySystem.class);
	}

	protected static RepositorySystemSession newSession(final RepositorySystem system) {
		final DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

		final LocalRepository localRepo = new LocalRepository(projectBuildDirectory + File.separator + "local-repo");
		session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

		return session;
	}

	protected void setVariableValueToObject(final Object object, final String variable, final Object value)
			throws IllegalAccessException {
		final Field field = ReflectionUtils.getFieldByNameIncludingSuperclasses(variable, object.getClass());
		field.setAccessible(true);
		field.set(object, value);
	}
}
