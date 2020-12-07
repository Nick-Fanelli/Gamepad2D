package gamepad.physics2D.forces;

import gamepad.physics2D.rigidbody.Rigidbody2D;

public interface ForceGenerator {

    void updateForce(Rigidbody2D body, float deltaTime);

}
