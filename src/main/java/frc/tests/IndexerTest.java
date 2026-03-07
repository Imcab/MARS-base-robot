package frc.tests;

import com.stzteam.mars.test.MARSTest;
import com.stzteam.mars.test.TestRoutine;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.core.modules.superstructure.modules.indexermodule.Indexer;
import frc.robot.core.requests.moduleRequests.IndexerRequestFactory;

@MARSTest(name = "Indexer test")
public class IndexerTest extends TestRoutine {
    private Indexer ind;

    public IndexerTest(Indexer ind) {
        this.ind = ind;
    }

    @Override
    public Command getRoutineCommand() {

        return Commands.sequence(

                run(IndexerRequestFactory.moveVoltage().withRollers(8).withIndex(8), ind),

                delay(4),

                run(IndexerRequestFactory.idle(), ind),

                delay(2),

                run(IndexerRequestFactory.moveVoltage().withRollers(-8).withIndex(8), ind),

                delay(4),

                run(IndexerRequestFactory.idle(), ind)

        );
    }

}
