// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.configuration.bindings;

import com.stzteam.mars.models.containers.Binding;
import com.stzteam.mars.operator.ControllerOI;
import com.stzteam.mars.services.nodes.Node;
import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.configuration.advantageScope.visuals.nodes.gamepiece.GamePieceNode.GamePieceMsg;
import frc.robot.configuration.advantageScope.visuals.nodes.trajectory.TrajectoryNode.TrajectoryMsg;
import frc.robot.configuration.constants.Constants;
import frc.robot.core.modules.superstructure.composite.Superstructure;
import frc.robot.core.requests.moduleRequests.ArmRequestFactory;
import frc.robot.core.requests.moduleRequests.FlyWheelRequestFactory;
import frc.robot.core.requests.moduleRequests.IndexerRequestFactory;

public class OperatorBindings implements Binding {

  private final ControllerOI operator;
  private final Superstructure superstructure;

  private Node<GamePieceMsg> gamePieceViz;
  private Node<TrajectoryMsg> trajectoryNode;

  private OperatorBindings(ControllerOI operator, Superstructure superstructure) {
    this.operator = operator;
    this.superstructure = superstructure;
  }

  public static OperatorBindings create(ControllerOI operator, Superstructure ss) {
    return new OperatorBindings(operator, ss);
  }

  public OperatorBindings withNodes(Node<GamePieceMsg> gp, Node<TrajectoryMsg> tr) {
    this.gamePieceViz = gp;
    this.trajectoryNode = tr;
    return this;
  }

  @Override
  public void bind() {
    var buttons = operator.getActionButtons();
    var bumpers = operator.getBumpers();
    // var driverSystem = operator.getSystemTriggers();
    var triggers = operator.getAnalogTriggers();
    var pov = operator.getDPadTriggers();

    // --------------------------------------------------------------- MANDO
    // ---------------------------------------------------------------

    // ----- Botones (a,b,x,y) -----
    /*
    buttons.bottom().whileTrue(intake.setControl(()-> IntakeRequestFactory.setAngle() //Bajar el intake (a)
    .withAngle(-130)
    .Tolerance(Constants.INTAKE_TOLERANCE)
    .withMode(intakeMODE.kDOWN)));

    buttons.top().whileTrue(intake.setControl(()-> IntakeRequestFactory.setAngle() //Bubir el intake (y)
    .withAngle(-10)
    .Tolerance(Constants.INTAKE_TOLERANCE)
    .withMode(intakeMODE.kUP)));    */

    buttons.right().whileTrue(superstructure.eatCommand()); // Comer fuels

    // ----- Botones (a,b,x,y) -----

    bumpers
        .right()
        .whileTrue(
            superstructure
                .getIndexer()
                .setControl(
                    () -> IndexerRequestFactory.moveVoltage().withIndex(-12).withRollers(-12)));
    bumpers.left().whileTrue(superstructure.clearFuel());

    pov.up()
        .whileTrue(
            superstructure.ShootAngleTest(
                () -> superstructure.getAngle(), () -> superstructure.getRPM()));
    pov.down().whileTrue(superstructure.lockToHub());
    pov.left()
        .whileTrue(
            superstructure
                .getFlywheelShooter()
                .setControl(() -> FlyWheelRequestFactory.moveVoltage().withVolts(-12)));

    triggers
        .right()
        .and(bumpers.right().negate())
        .whileTrue(
            superstructure.shootOnTheMove(
                superstructure.getVirtualTarget(),
                ArmRequestFactory.interpolateTarget()
                    .withDistance(() -> superstructure.getVirtualDistance())
                    .withTolerance(Constants.ARM_TOLERANCE),
                FlyWheelRequestFactory.interpolateRPM()
                    .withDistance(() -> superstructure.getVirtualDistance())
                    .withTolerance(Constants.FLYWHEEL_TOLERANCE),
                12));

    triggers
        .right()
        .and(bumpers.right())
        .whileTrue(
            superstructure.shootOnTheMove(
                superstructure.getVirtualTarget(),
                ArmRequestFactory.interpolateTarget()
                    .withDistance(() -> superstructure.getVirtualDistance())
                    .withTolerance(Constants.ARM_TOLERANCE),
                FlyWheelRequestFactory.interpolateRPM()
                    .withDistance(() -> superstructure.getVirtualDistance())
                    .withTolerance(Constants.FLYWHEEL_TOLERANCE),
                -12));

    triggers
        .left()
        .and(bumpers.right().negate())
        .whileTrue(
            superstructure.shootOnTheMove(
                new Translation2d(0.863, 4.003),
                ArmRequestFactory.setAngle().withAngle(0),
                FlyWheelRequestFactory.setRPM().toRPM(-4000).withTolerance(50),
                12));

    triggers
        .left()
        .and(bumpers.right())
        .whileTrue(
            superstructure.shootOnTheMove(
                new Translation2d(0.863, 4.003),
                ArmRequestFactory.setAngle().withAngle(0),
                FlyWheelRequestFactory.setRPM().toRPM(-4000).withTolerance(50),
                -12));

    // --------------------------------------------------------------- MANDO
    // ---------------------------------------------------------------

  }
}
