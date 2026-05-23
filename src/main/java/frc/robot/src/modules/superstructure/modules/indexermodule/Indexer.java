// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.src.modules.superstructure.modules.indexermodule;

import com.stzteam.features.dictionary.Dictionary.CommonTables;
import com.stzteam.forgemini.io.NetworkIO;
import com.stzteam.mars.diagnostics.ModuleColorCode;
import com.stzteam.mars.diagnostics.StatusColorCode.Severity;
import com.stzteam.mars.models.SubsystemBuilder;
import com.stzteam.mars.models.Telemetry;
import com.stzteam.mars.models.singlemodule.ModularSubsystem;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.configuration.KeyManager;
import frc.robot.src.modules.superstructure.modules.indexermodule.IndexerIO.IndexerInputs;
import frc.robot.src.requests.moduleRequests.IndexerCommands;
import frc.robot.src.requests.moduleRequests.IndexerRequest;
import frc.robot.src.requests.moduleRequests.IndexerRequestFactory;
import java.util.function.Supplier;

public class Indexer extends ModularSubsystem<IndexerInputs, IndexerIO> implements IndexerCommands {

  public static final ModuleColorCode IDLE =
      ModuleColorCode.solid("IDLE", Severity.OK, Color.kDarkGreen, "Indexer en reposo");
  public static final ModuleColorCode VOLTAGE =
      ModuleColorCode.solid("VOLTAGE", Severity.OK, Color.kFirstBlue, "Indexer en voltaje");
  public static final ModuleColorCode SPEED =
      ModuleColorCode.solid("SPEED", Severity.OK, Color.kYellow, "Indexer en velocidad");
  public static final ModuleColorCode STOPED =
      ModuleColorCode.solid("STOPED", Severity.WARNING, Color.kRed, "Indexer detenido");

  public Indexer(IndexerIO io) {

    super(
        SubsystemBuilder.<IndexerInputs, IndexerIO>setup()
            .key(KeyManager.INDEX_KEY)
            .hardware(io, new IndexerInputs())
            .request(IndexerRequestFactory.idle())
            .telemetry(new IndexerTelemetry()));

    this.setDefaultCommand(runRequest(() -> IndexerRequestFactory.idle()));
  }

  @Override
  public Command setControl(Supplier<IndexerRequest> request) {
    return runRequest(request);
  }

  @Override
  public IndexerInputs getState() {
    return inputs;
  }

  public static class IndexerTelemetry extends Telemetry<IndexerInputs> {

    private static final String VELOCITY_INDEX_KEY = CommonTables.VELOCITY_KEY + "Index";
    private static final String VELOCITY_ROLL_KEY = CommonTables.VELOCITY_KEY + "Roll";

    @Override
    public void telemeterize(IndexerInputs data) {
      NetworkIO.set(KeyManager.INDEX_KEY, VELOCITY_INDEX_KEY, data.velocityIndex);
      NetworkIO.set(KeyManager.INDEX_KEY, VELOCITY_ROLL_KEY, data.velocityRoll);

      NetworkIO.set(KeyManager.INDEX_KEY, "Current", data.current);
    }
  }

  @Override
  public void absolutePeriodic(IndexerInputs inputs) {}

  @Override
  public void simulationPeriodic() {}
}
